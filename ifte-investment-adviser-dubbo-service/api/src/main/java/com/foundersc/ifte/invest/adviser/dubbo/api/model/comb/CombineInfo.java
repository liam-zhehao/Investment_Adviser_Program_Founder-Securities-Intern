package com.foundersc.ifte.invest.adviser.dubbo.api.model.comb;

import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombProfitTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 组合信息
 *
 * @author wangfuwei
 * @date 2022/9/26
 */
@Data
public class CombineInfo implements Serializable {
    private static final long serialVersionUID = -3294041107522401888L;
    /**
     * 组合名称
     */
    private String combineName;

    /**
     * 组合代码
     */
    private String combineCode;

    /**
     * 投顾机构名称
     */
    private String investOrganName;

    /**
     * 组合简介
     */
    private String combineDesc;

    /**
     * 组合管理模式
     */
    private String combEntrustMode;

    /**
     * 组合风险等级
     */
    private String combRiskLevel;

    /**
     * 组合年化收益率
     */
    private BigDecimal comIncomeRatio;

    /**
     * 组合最大回撤率
     */
    private BigDecimal comMaxRetreatRatio;

    /**
     * 建议持有期限
     */
    private String adviseHoldTerm;

    /**
     * 基准编号
     */
    private String benchmarkNo;

    /**
     * 基准名称
     */
    private String benchmarkName;

    /**
     * 组合交易状态：0 允许交易；1 不允许交易
     */
    private String combTradeStatus;

    /**
     * 组合来源编号
     */
    private String sourceId;
    /**
     * 外部组合代码
     */
    private String extCombineCode;
    /**
     * 组合状态
     */
    private String combCodeStatus;
    /**
     * 组合上下架状态
     */
    private String combShelfStatus;
    /**
     * 组合费用编号
     */
    private Integer combChargeNo;
    /**
     * 组合投资类型
     */
    private String combInvestType;
    /**
     * 开始有效日期
     */
    private Integer beginValidDate;
    /**
     * 结束有效日期
     */
    private Integer endValidDate;
    /**
     * 组合投资期限
     */
    private String combInvestTerm;
    /**
     * 组合投资类别
     */
    private String combInvestKind;
    /**
     * 最小签约金额
     */
    private BigDecimal minSignBalance;
    /**
     * 最小追加金额
     */
    private BigDecimal minAddBalance;
    /**
     * 返款天数
     */
    private Integer fundBackDays;
    /**
     * 最小持有天数
     */
    private Integer minHoldDays;
    /**
     * 拼音代码
     */
    private String spellCode;
    /**
     * 折扣比率
     */
    private BigDecimal discountRate;

    /**
     * 目标盈折扣费率
     */
    private BigDecimal targetDiscountRate;
    /**
     * 组合累计收益率
     */
    private BigDecimal comSumIncomeRatio;
    /**
     * 标签json
     */
    private String tagJson;
    /**
     * 配置理念
     */
    private String configurationInstructions;
    /**
     * 最大买入金额
     */
    private BigDecimal maxBuyBalance;
    /**
     * 组合来源名称
     */
    private String sourceName;
    /**
     * 底仓产品代码
     */
    private String basicProdCode;
    /**
     * 底仓产品TA编号
     */
    private String basicProdtaNo;
    /**
     * 冷静期天数
     */
    private Integer coolingDays;

    /**
     * 投资策略描述
     */
    private String investStrategy;
    /**
     * 投资范围
     */
    private String investRange;
    /**
     * 组合策略类型
     */
    private String combStrategyType;
    /**
     * 适合投资者范围
     */
    private String suitableRange;
    /**
     * 最小减少金额
     */
    private BigDecimal minReduceBalance;
    /**
     * 最小保留金额
     */
    private BigDecimal minRetainBalance;
    /**
     * 组合买入单位
     */
    private BigDecimal combBuyUnit;
    /**
     * 投顾机构编号
     */
    private String investOrganNo;
    /**
     * 总额度
     */
    private BigDecimal totalQuota;
    /**
     * 是否成立满一年
     */
    private Boolean setUpMoreThanOneYear;
    /**
     * 成立日期
     */
    private Integer setUpDate;

    /**
     * 组合当日收益率
     */
    private String todayIncomeRatio;

    /**
     * 处理状态
     */
    private String dealStatus;

    /**
     * 目标年化收益率
     */
    private String targetProfitRatio;

    /**
     * 购买开始日期
     */
    private Integer buyStartDate;

    /**
     * 购买开始时间
     */
    private Integer buyStartTime;

    /**
     * 购买截止日期
     */
    private Integer buyEndDate;

    /**
     * 购买截止时间
     */
    private Integer buyEndTime;

    /**
     * 组合收益类型
     */
    private String combProfitType;

    /**
     * 止盈生效日期
     */
    private Integer profitValidDate;

    /**
     * 止盈生效日期
     */
    private Integer profitValidTime;
    /**
     * 运作开始日期
     */
    private Integer operationalStartDate;

    /**
     * 运作开始时间
     */
    private Integer operationalStartTime;
    /**
     * 策略终止日期
     */
    private Integer combEndDate;
    /**
     * 组合终止时间
     */
    private Integer combEndTime;

    /**
     * 运作不止盈日期
     */
    private Integer stopProfitDate;
    /**
     * 运作不止盈时间
     */
    private Integer stopProfitTime;

    /**
     * 止盈续存模式
     */
    private String stopProfitContinueMode;

    /**
     * 到期续存模式
     */
    private String operationalDealContinueMode;

    /**
     * 运作到期处理模式
     */
    private String operationalDealMode;

    /**
     * 当前期数
     */
    private Integer combPeriod;

    /**
     * 是否末期目标盈
     */
    private Boolean endPeriod;

    /**
     * 目标盈组合全称：目标应组合+期数+投顾机构名称
     *
     * @return
     */
    public String getTargetCombFullName() {
        if (!isTargetComb()) {
            return combineName;
        }
        StringBuilder sb = new StringBuilder(combineName);
        if (StringUtils.isNotBlank(this.investOrganName)) {
            sb.append("-").append(this.investOrganName);
        }
        return sb.toString();
    }

    /**
     * 获取当前期数：小于10补0
     * 例如：1返回01期
     *
     * @return
     */
    private String getCombPeriodStr() {
        if (combPeriod == null) {
            return StringUtils.EMPTY;
        }
        String prefix = "";
        if (combPeriod.intValue() < 10) {
            prefix = "0";
        }
        return prefix + combPeriod + "期";
    }

    /**
     * 是否为目标盈组合
     *
     * @return
     */
    public boolean isTargetComb() {
        return CombProfitTypeEnum.isTargetComb(this.combProfitType);
    }
}
