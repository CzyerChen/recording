package com.learning.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * Desciption
 *
 * @author Claire.Chen
 * @create_time 2019 -01 - 29 13:29
 */
public class RedisLock {
    private static Logger log = LoggerFactory.getLogger(RedisLock.class);
    private StringRedisTemplate redisTemplate;

    /**
     * 加锁
     * @param key
     * @param value 当前时间+超时时间
     * @return
     */
    public boolean lock(String key, String value) {

        if(redisTemplate.opsForValue().setIfAbsent(key, value)) {//相当于SETNX指令，setIfAbsent方法设置了返回true,没有设置返回false
            return true;
        }
        String currentValue = redisTemplate.opsForValue().get(key);
        //如果锁过期  解决死锁
        if (!StringUtils.isEmpty(currentValue)
                && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //获取上一个锁的时间，锁过期后，GETSET将原来的锁替换成新锁
            String oldValue = redisTemplate.opsForValue().getAndSet(key, value);
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                return true;
            }

        }

        return false;//拿到锁的就有执行权力，拿不到的只有重新再来
    }

    /**
     * 解锁
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = redisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                redisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch (Exception e) {
            log.error("【redis分布式锁】解锁异常, {}", e);
        }
    }




}
