package com.foundersc.ifte.invest.adviser.web.service;

import java.time.Duration;

/**
 * 幂等性服务
 */
public interface IdempotentService {
    /**
     * 幂等性校验
     *
     * @param idemUniqKey 幂等性校验的key，各种业务应该有不同的前缀，防止重复
     * @param requestId
     * @param expireTime
     * @return
     */
    boolean check(String idemUniqKey, String requestId, Duration expireTime);

    /**
     * 重置幂等
     *
     * @param idemUniqKey 幂等性校验的key，各种业务应该有不同的前缀，防止重复
     * @param requestId
     * @return
     */
    boolean reset(String idemUniqKey, String requestId);
}
