package com.muke.buying.redis;

import com.muke.buying.model.User;
import com.muke.buying.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisServiceTest {
    @Autowired
    private RedisService redisService;

    @Test
    public void test() {
        User user = new User();
        user.setId(1);
        user.setName("xiaofang");
        redisService.set("xiaofang", user);
        Object xiaofang = redisService.get("xiaofang");
        assertEquals("{\"id\":1,\"name\":\"xiaofang\"}",String.valueOf(xiaofang));
   }
}