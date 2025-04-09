package com.foundersc.ifte.invest.adviser.web.model.combine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.AdviseHoldTermEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombCodeStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombEntrustModeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombShelfStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombInvestKindEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombInvestTermEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombRiskLevelEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombProfitTypeEnum;
import com.foundersc.ifte.invest.adviser.web.annotation.MetaData;
import com.foundersc.ifte.invest.adviser.web.util.DataUtil;
import com.foundersc.ifte.invest.adviser.web.util.RatioUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.web.constants.TipConstants.NOT_SHOW_TIP;

/**
 * 组合基本信息
 */
@Data
@ApiModel(value = "CombineInfoVO", description = "组合基本信息")
public class CombineInfoVO {
    @ApiModelProperty("是否需要签约，初次购买需要签约，且使用最少签约金额约束")
    private Boolean needSign;

    @ApiModelProperty(value = "组合名称", required = true)
    private String combineName;

    @ApiModelProperty(value = "组合代码", required = true)
    private String combineCode;

    @ApiModelProperty(value = "投顾机构名称", required = true)
    private String investOrganName;

    @ApiModelProperty(value = "投顾机构编号", required = true)
    private String investOrganNo;

    @ApiModelProperty(value = "组合简介", required = true)
    private String combineDesc;

    @ApiModelProperty(value = "组合管理模式", required = true)
    @MetaData(clazz = CombEntrustModeEnum.class)
    private String combEntrustMode;

    @ApiModelProperty(value = "组合风险等级", required = true)
    @MetaData(clazz = CombRiskLevelEnum.class)
    private String combRiskLevel;

    @ApiModelProperty(value = "组合年化收益率", required = true)
    private BigDecimal comIncomeRatio;

    @ApiModelProperty(value = "组合年化收益率描述", required = true)
    private String comIncomeRatioDesc;

    @ApiModelProperty(value = "组合最大回撤率")
    private BigDecimal comMaxRetreatRatio;

    @ApiModelProperty(value = "组合最大回撤率描述")
    private String comMaxRetreatRatioDesc;

    @ApiModelProperty(value = "建议持有期限")
    @MetaData(clazz = AdviseHoldTermEnum.class)
    private String adviseHoldTerm;

    @ApiModelProperty("基准编号")
    private String benchmarkNo;

    @ApiModelProperty("基准名称")
    private String benchmarkName;

    @ApiModelProperty("组合状态")
    @MetaData(clazz = CombCodeStatusEnum.class)
    private String combCodeStatus;

    @ApiModelProperty("组合上下架状态")
    @MetaData(clazz = CombShelfStatusEnum.class)
    private String combShelfStatus;

    @ApiModelProperty(value = "组合交易状态：0 允许交易；1 不允许交易")
    private String combTradeStatus;

    @ApiModelProperty(value = "组合投资期限")
    @MetaData(clazz = CombInvestTermEnum.class)
    private String combInvestTerm;

    @ApiModelProperty(value = "组合投资品种")
    @MetaData(clazz = CombInvestKindEnum.class)
    private String combInvestKind;

    @ApiModelProperty("最少签约金额")
    private BigDecimal minSignBalance;

    @ApiModelProperty("最小追加金额")
    private BigDecimal minAddBalance;

    @ApiModelProperty("最大转入金额")
    private BigDecimal maxBuyBalance;

    @ApiModelProperty("最小转入金额")
    private BigDecimal minBuyBalance;

    @ApiModelProperty("组合费用编号")
    private Integer combChargeNo;

    @ApiModelProperty("行情展示区间")
    private List<CombTrendRangeVO> combTrendRanges;

    @ApiModelProperty("默认行情展示区间，客户持仓页展示用")
    private List<CombTrendRangeVO> defaultCombTrendRanges;

    @ApiModelProperty("购买页面url")
    private String purchaseUrl;

    @ApiModelProperty("购买协议url")
    private String agreementUrl;

    @ApiModelProperty("private String tagId;")
    private String tagJson;

    @ApiModelProperty(value = "投顾服务费", example = "0.25%")
    private String adviseFee;

    @ApiModelProperty(value = "运营标签")
    private List<String> opeTags;

    @ApiModelProperty(value = "声明")
    private List<String> statements;


    @ApiModelProperty(value = "是否成立满一年，成立不满一年时comIncomeRatioDesc和comMaxRetreatRatioDesc特殊处理")
    private Boolean setUpMoreThanOneYear;

    @ApiModelProperty(value = "组合成立日期")
    private Integer setUpDate;

    @ApiModelProperty(value = "展示值")
    private String displayValue;

    @ApiModelProperty(value = "展示描述")
    private String displayDesc;

    @ApiModelProperty(value = "策略到期日期")
    private String combEndDate;

    @ApiModelProperty(value = "购买开始日期")
    private String buyStartDate;

    @ApiModelProperty(value = "购买截止日期")
    private String buyEndDate;

    @ApiModelProperty(value = "运作开始日期")
    private String operationalStartDate;

    @ApiModelProperty(value = "运作不止盈日期")
    private String stopProfitDate;

    @ApiModelProperty(value = "止盈开始日期")
    private String stopProfitBeginDate;

    @ApiModelProperty(value = "止盈生效日期")
    private String profitValidDate;

    @ApiModelProperty(value = "当前日期")
    private String currentDate;

    @ApiModelProperty(value = "策略特点")
    private StrategyTraitVO strategyTrait;

    @ApiModelProperty(value = "运作周期")
    private String operationPeriod;

    @ApiModelProperty("处理状态")
    private String dealStatus;

    @ApiModelProperty("处理状态描述")
    private String dealStatusDesc;

    @ApiModelProperty("是否能购买")
    private Integer canBuy;

    @ApiModelProperty("组合收益类型")
    private String combProfitType;

    @JsonIgnore
    private String buyStartTime;

    @JsonIgnore
    private String buyEndTime;

    /**
     * 计算用的投顾服务费
     */
    @JsonIgnore
    private BigDecimal calAdviseFee;


    public String getComIncomeRatioDesc() {
        if (Boolean.TRUE.equals(setUpMoreThanOneYear)) {
            return RatioUtil.formatPercentWithPrefix(comIncomeRatio);
        }
        return NOT_SHOW_TIP;
    }

    public String getComMaxRetreatRatioDesc() {
        if (Boolean.TRUE.equals(setUpMoreThanOneYear)) {
            return RatioUtil.formatPercent(comMaxRetreatRatio);
        }
        return NOT_SHOW_TIP;
    }

    public CombineInfoVO() {
        this.combTrendRanges = new ArrayList<>();
        this.defaultCombTrendRanges = new ArrayList<>();
        this.opeTags = new ArrayList<>();
        this.statements = new ArrayList<>();
    }

    /**
     * 注意：该方法的调用依赖于needSign字段
     *
     * @return
     */
    public BigDecimal getMinBuyBalance() {
        if (Boolean.TRUE.equals(this.needSign)) {
            // 签约购买时，默认同时校验首次买入金额和单笔最小买入金额
            this.minBuyBalance = DataUtil.getMax(getMinSignBalance(), getMinAddBalance());
            return this.minBuyBalance;
        }
        // 追加购买时，返回最小追加金额
        this.minBuyBalance = getMinAddBalance();
        return minBuyBalance;
    }

    /**
     * 最小签约金额，向上取整
     *
     * @return
     */
    public BigDecimal getMinSignBalance() {
        return minSignBalance == null ? null : minSignBalance.setScale(0, RoundingMode.CEILING);
    }

    /**
     * 最小追加金额，向上取整
     *
     * @return
     */
    public BigDecimal getMinAddBalance() {
        return minAddBalance == null ? null : minAddBalance.setScale(0, RoundingMode.CEILING);
    }

    /**
     * 最大金额，向下取整
     *
     * @return
     */
    public BigDecimal getMaxBuyBalance() {
        return maxBuyBalance == null ? null : maxBuyBalance.setScale(0, RoundingMode.FLOOR);
    }

    @JsonIgnore
    public boolean isTargetComb() {
        return CombProfitTypeEnum.TARGET_COMB.getTypeId().equals(this.getCombProfitType());
    }
}
