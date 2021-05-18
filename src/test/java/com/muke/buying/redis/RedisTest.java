 package com.muke.buying.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * 使用Lettuce操作redis
 */
public class RedisTest {
    private static RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    @BeforeEach
    public void before() {
        RedisURI redisUri = RedisURI.builder()
                .withHost("127.0.0.1").withPort(6379).withPassword("password")
                .withTimeout(Duration.of(20, ChronoUnit.SECONDS))
                .build();
        client = RedisClient.create(redisUri);
        connection = client.connect();
    }

    @AfterEach
    public void after() {
        connection.close();
        client.shutdown();
    }

    /**
     * 同步操作
     * api和jedis很类型
     */
    @Test
    public void sync() {
        RedisCommands<String, String> commands = connection.sync();
        String result = commands.set("name", "mayun");
        assertEquals("OK", result);
        assertEquals("mayun", commands.get("name"));

        /**
         * ex(long timeout)设置指定的到期时间（以秒为单位）
         * nx() 仅在不存在的情况下设置
         * px(long timeout) 设置指定的到期时间（以毫秒为单位）
         * xx() 仅在已存在的情况下设置
         */
        SetArgs args = SetArgs.Builder.nx().ex(10);
        commands.set("name", "xiaoming", args);
        assertEquals("mayun", commands.get("name"));

        args = SetArgs.Builder.xx().ex(10);
        commands.set("name", "xiaoming", args);
        assertEquals("xiaoming", commands.get("name"));

        args = SetArgs.Builder.xx().ex(10);
        commands.set("age", "10", args);
        assertEquals(null, commands.get("age"));
    }

    /**
     * 异步操作
     */
    @Test
    public void async() throws Exception {
        RedisAsyncCommands<String, String> commands = connection.async();
        Future<String> future = commands.set("name", "mayun");
        assertEquals("OK", future.get());

        SetArgs args = SetArgs.Builder.nx().ex(10);
        future = commands.set("age", "30", args);
        assertEquals("OK", future.get());
    }

    /**
     * 响应式API
     */
    @Test
    public void reactive() throws Exception {
        RedisReactiveCommands<String, String> commands = connection.reactive();
        Mono<String> result = commands.set("name", "mayun");
        assertEquals("OK", result.block());

        SetArgs args = SetArgs.Builder.nx().ex(10);
        result = commands.set("age", "30", args);
        result.subscribe(value -> assertEquals("OK", value));

        //开启一个事务，先把counter设置为1，再将counter自增1
        commands.multi().doOnSuccess(r -> {
            commands.set("count", "1").doOnNext(value -> assertEquals("OK", value)).subscribe();
            commands.incr("count").doOnNext(value -> assertEquals(2, value)).subscribe();
        }).flatMap(s -> commands.exec())
                .doOnNext(transactionResult -> assertEquals(false, transactionResult.wasDiscarded())).subscribe();

        Thread.sleep(1000 * 5);
    }
}
