package com.foundersc.ifte.invest.adviser.dubbo.api.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.TradeTimeCheckRes;

/**
 * @author wangfuwei
 * @date 2023/10/24
 */
public interface TradeTimeRemoteService {
    /**
     * 检查交易时间及是否支持7*24
     *
     * @param appVersion
     * @return
     */
    TradeTimeCheckRes checkTradeTime(String appVersion);

    /**
     * 检查交易时间及是否支持7*24
     *
     * @param simpleAccount
     * @param appVersion
     * @return
     */
    TradeTimeCheckRes checkTradeTime(SimpleAccount simpleAccount, String appVersion);
}
