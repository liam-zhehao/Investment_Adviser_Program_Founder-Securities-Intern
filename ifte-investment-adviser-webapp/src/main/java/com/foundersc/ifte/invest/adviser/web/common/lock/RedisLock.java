package com.foundersc.ifte.invest.adviser.web.common.lock;

import com.foundersc.ifte.invest.adviser.web.constants.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * 基于redis的简单锁实现
 */
@Component
public class RedisLock {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * lua脚本：判断指定key的value是否为指定值，如果是，删除并返回true，否则返回false
     */
    private final String deleteIfEqualScriptStr = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 如果匹配则删除
     */
    private final RedisScript<Boolean> deleteIfEqualScript = new DefaultRedisScript<>(deleteIfEqualScriptStr, Boolean.class);

    /**
     * 尝试加锁
     *
     * @param lockKey    锁的key
     * @param owner      锁的拥有者
     * @param expireTime 加锁成功之后锁的过期时间（自动释放时间）
     * @return
     */
    public boolean tryLock(String lockKey, String owner, Duration expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(getRedisLockKey(lockKey), owner, expireTime);
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁的key
     * @param owner   锁的拥有者，只有锁的拥有者才能释放锁
     */
    public boolean unlock(String lockKey, String owner) {
        List<String> keys = Arrays.asList(getRedisLockKey(lockKey));
        return redisTemplate.execute(deleteIfEqualScript, keys, owner);
    }

    /**
     * redis中的key
     *
     * @param lockKey
     * @return
     */
    private String getRedisLockKey(String lockKey) {
        return RedisConstants.REDIS_LOCK_PREFIX + lockKey;
    }
}
