package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientCombAsset implements Serializable {
    /**
     * 组合代码
     */
    private String combineCode;

    /**
     * 组合名称
     */
    private String combineName;

    /**
     * 资产单元
     */
    private String assetUnit;

    /**
     * 投顾账户
     */
    private String investorAccount;

    /**
     * 组合总资产 包括未确认的在途金额
     */
    private Double combineAsset;

    /**
     * 组合市值
     */
    private Double combMarketVal;

    /**
     * 最新单日收益
     */
    private Double lastDayIncome;

    /**
     * 最新收益日期
     */
    private Integer lastIncomeDate;

    /**
     * 组合累计盈亏
     */
    private Double combSumIncome;

    /**
     * 持仓收益率
     */
    private String combHoldIncomeRatio;

    /**
     * 收益预估标志
     */
    private Integer incomePredictFlag;


    /**
     * 预估收益日期
     */
    private Integer incomePredictDate;

    /**
     * 目标盈组合全称
     */
    private volatile String targetCombFullName;

    /**
     * 是否为目标盈组合
     */
    private volatile Boolean targetComb;

    /**
     * 组合持仓年化收益率
     */
    private Double combHoldYearIncomeRatio;

    /**
     * 组合风险等级
     */
    private volatile String combRiskLevel;

    /**
     * 购买开始日期
     */
    private volatile Integer buyStartDate;

    /**
     * 购买开始时间
     */
    private volatile Integer buyStartTime;

    /**
     * 购买截止日期
     */
    private volatile Integer buyEndDate;

    /**
     * 购买截止时间
     */
    private volatile Integer buyEndTime;

    /**
     * 止盈生效日期
     */
    private volatile Integer profitValidDate;

    /**
     * 运作开始日期
     */
    private volatile Integer operationalStartDate;

    /**
     * 运作开始时间
     */
    private volatile Integer operationalStartTime;

    /**
     * 策略终止日期
     */
    private volatile Integer combEndDate;

    /**
     * 组合终止时间
     */
    private volatile Integer combEndTime;

    /**
     * 运作不止盈日期
     */
    private volatile Integer stopProfitDate;

    /**
     * 运作不止盈时间
     */
    private volatile Integer stopProfitTime;
    /**
     * 处理状态：0-未运作，1-运作期，2-已止盈，3-止盈中，4-已到期
     */
    private volatile String dealStatus;

    /**
     * 最新收益更新状态  1：已有今日最新收益，2：未更新今日收益但有今日预估收益，3:既没有今日最新收益，也没有今日预估收益，4:组合持仓，还未运作时
     */
    private int incomeUpdateStatus;
}
