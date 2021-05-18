package com.muke.buying.service;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RedisService {
    private RedisTemplate<String, String> template;

    public RedisService(RedisTemplate<String, String> template) {
        this.template = template;
    }

    public boolean existsKey(String key) {
        return template.hasKey(key);
    }

    /**
     * 写入缓存
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = template.opsForValue();
            operations.set(key, JSON.toJSONString(value));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = template.opsForValue();
            operations.set(key, JSON.toJSONString(value));
            template.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 读取缓存
     */
    public Object get(final String key) {
        ValueOperations<String, String> operations = template.opsForValue();
        return operations.get(key);
    }

    /**
     * 删除key
     */
    public boolean del(String key) {
        return template.delete(key);
    }

    /**
     * 删除多个key
     */
    public void del(String... keys) {
        Set<String> kSet = Stream.of(keys).collect(Collectors.toSet());
        template.delete(kSet);
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = template.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = template.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * 列表添加
     *
     * @param k
     * @param v
     */
    public void lPush(String k, Object v) {
        ListOperations<String, String> list = template.opsForList();
        list.rightPush(k, JSON.toJSONString(v));
    }

    /**
     * 列表获取
     *
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<String> lRange(String k, long l, long l1) {
        ListOperations<String, String> list = template.opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    public void expireKeyAt(String key, Date date) {
        template.expireAt(key, date);
    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
    public void add(String key, Object value) {
        SetOperations<String, String> set = template.opsForSet();
        set.add(key, JSON.toJSONString(value));
    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
    public Set<String> setMembers(String key) {
        SetOperations<String, String> set = template.opsForSet();
        return set.members(key);
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @param timeUnit
     * @return
     */
    public long getKeyExpire(String key, TimeUnit timeUnit) {
        return template.getExpire(key, timeUnit);
    }
    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, String> zset = template.opsForZSet();
        zset.add(key, JSON.toJSONString(value), scoure);
    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<String> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, String> zset = template.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }
}
