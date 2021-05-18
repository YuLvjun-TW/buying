package com.muke.buying.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * 缓存配置-使用Lettuce客户端，自动注入配置的方式
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    /**
     * 自定义缓存key的生成策略。默认的生成策略是看不懂的(乱码内容) 通过Spring 的依赖注入特性进行自定义的配置注入并且此类是一个配置类可以更多程度的自定义配置
     *
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    /**
     * 缓存配置管理器
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory factory) {
        //以锁写入的方式创建RedisCacheWriter对象
        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(factory);
        /**
         * 设置CacheManager的Value序列化方式为JdkSerializationRedisSerializer,
         *         但其实RedisCacheConfiguration默认就是使用
         *         StringRedisSerializer序列化key，
         *         JdkSerializationRedisSerializer序列化value,
         *         所以以下注释代码就是默认实现，没必要写，直接注释掉
         */
        // RedisSerializationContext.SerializationPair pair = RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(this.getClass().getClassLoader()));
        // RedisCacheConfiguration redis = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        //创建默认缓存配置对象
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        return new RedisCacheManager(writer, config);
    }

    /**
     * 获取缓存操作助手对象
     *
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        //创建Redis缓存操作助手RedisTemplate对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        //以下代码为将RedisTemplate的Value序列化方式由JdkSerializationRedisSerializer更换为Jackson2JsonRedisSerializer
        //此种序列化方式结果清晰、容易阅读、存储字节少、速度快，所以推荐更换
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        /**
         * om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL); deprecated
         * 该方法是指定序列化输入的类型，就是将数据库里的数据按照一定类型存储到redis缓存中。
         * 如果注释掉，那存储到redis里的数据将是没有类型的纯json，我们调用redis API获取到数据后，
         * java解析将是一个LinkHashMap类型的key-value的数据结构，我们需要使用的话就要自行解析，这样增加了编程的复杂度。
         *
         * 存储到redis里的数据将是有类型的json数据，例如：
         * ["java.util.ArrayList",[{"@class":"com.model.app","id":72,"uuid":"c4d7fc52-4096-4c79-81ef-32cb1b87fd28","type":2}]]
         * 这样java获取到数据后，将会将数据自动转化为java.util.ArrayList和com.model.app，方便直接使用。
         *
         * https://blog.csdn.net/zzhongcy/article/details/105813105
         */
        om.activateDefaultTyping( LaissezFaireSubTypeValidator.instance ,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
