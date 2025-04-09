package com.foundersc.ifte.invest.adviser.web.service.impl.idem;

import com.foundersc.ifte.invest.adviser.web.common.lock.RedisLock;
import com.foundersc.ifte.invest.adviser.web.service.IdempotentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 基于redis锁的幂等性实现
 *
 * @author wangfuwei
 * @date 2022/11/4
 */
@Service("redisLockIdempotentService")
@Slf4j
public class RedisLockIdempotentService implements IdempotentService {
    @Autowired
    private RedisLock redisLock;

    @Override
    public boolean check(String idemUniqKey, String requestId, Duration expireTime) {
        boolean result = redisLock.tryLock(idemUniqKey, requestId, expireTime);
        if (!result) {
            log.warn("idempotentCheck fail, uniqKey={}, requestId={}, expireSeconds={}s", idemUniqKey, requestId, expireTime.getSeconds());
        } else {
            log.info("idempotentCheck success, uniqKey={}, requestId={}, expireSeconds={}s", idemUniqKey, requestId, expireTime.getSeconds());
        }
        return result;
    }

    @Override
    public boolean reset(String idemUniqKey, String requestId) {
        return redisLock.unlock(idemUniqKey, requestId);
    }
}
