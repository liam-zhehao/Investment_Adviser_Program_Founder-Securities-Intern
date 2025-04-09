package com.foundersc.ifte.invest.adviser.dubbo.api.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangfuwei
 * @date 2023/10/24
 */
@Data
public class TradeTimeCheckRes implements Serializable {
    /**
     * 是否支持7*24
     */
    private boolean support_7_24;

    /**
     * 是否在交易时间
     */
    private boolean inTradeTime;

    /**
     * 提示
     */
    private String tip;
}
