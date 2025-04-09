package com.foundersc.ifte.invest.adviser.dubbo.api.model.comb;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CombStrategyInfo implements Serializable {

    /**
     * 组合代码
     */
    private String combineCode;

    /**
     * 组合名称
     */
    private String combineName;

    /**
     * 更新日期
     */
    private Integer updateDate;

    /**
     * 更新时间
     */
    private Integer updateTime;

    /**
     * 购买开始日期
     */
    private Integer buyStartDate;

    /**
     * 购买结束日期
     */
    private Integer buyEndDate;

    /**
     * 购买开始时间
     */
    private Integer buyStartTime;

    /**
     * 购买结束时间
     */
    private Integer buyEndTime;

    /**
     * 止盈年化收益率
     */
    private BigDecimal targetProfitRatio;

    /**
     * 组合期编号
     */
    private Integer combPeriod;

    /**
     * 处理状态
     *
     * @see com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombDealStatusEnum
     */
    private String dealStatus;

    /**
     * 是否末期目标盈
     */
    private String isEndPeriod;

    /**
     * 运作开始日期
     */
    private Integer operationalStartDate;

    /**
     * 运作开始时间
     */
    private Integer operationalStartTime;

    /**
     * 期数
     */
    private Integer periods;

    /**
     * 运作不止盈日期
     */
    private Integer stopProfitDate;

    /**
     * 运作不止盈时间
     */
    private Integer stopProfitTime;

    /**
     * 组合终止日期
     */
    private Integer combEndDate;

    /**
     * 组合终止时间
     */
    private Integer combEndTime;

    /**
     * 止盈生效日期
     */
    private Integer profitValidDate;

    /**
     * 止盈生效时间
     */
    private Integer profitValidTime;

    /**
     * 修改日期时间
     */
    private String modifyDateTime;

    /**
     * 参与人数
     */
    private Integer numberOfParticipants;

    /**
     * 开始有效日期
     */
    private Integer beginValidDate;

    /**
     * 结束有效日期
     */
    private Integer endValidDate;

}
