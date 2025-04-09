package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 客户总资产
 */
@Data
public class ClientTotalAsset implements Serializable {
    /**
     * 组合资产 包括在途
     */
    private BigDecimal combAsset;

    /**
     * 组合当日盈亏
     */
    private BigDecimal combTodayIncome;

    /**
     * 组合累计盈亏
     */
    private BigDecimal combSumIncome;

    /**
     * 最新单日收益
     */
    private BigDecimal lastDayIncome;

    /**
     * 最新单日收益率
     */
    private String lastDayIncomeRatio;

    /**
     * 最新收益日期
     */
    private int lastIncomeDate;

    /**
     * 累计收益率
     */
    private String totalIncomeRatio;

    /**
     * 未扣取费用
     */
    private BigDecimal unPayFare;

    /**
     * 累计当前金额
     */
    private BigDecimal sumCurrentBalance;

    /**
     * 累计组合市值
     */
    private BigDecimal sumCombMarket;

    /**
     * 客户累计收益
     */
    private BigDecimal clientSumIncome;

    /**
     * 累计修正金额
     */
    private BigDecimal sumCorrectBalance;

    /**
     * 预估分红金额
     */
    private BigDecimal preDividendBalance;
}
