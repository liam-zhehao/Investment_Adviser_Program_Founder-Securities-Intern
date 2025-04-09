package com.foundersc.ifte.invest.adviser.web.model.trade;

import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombInvestKindEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombInvestTermEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombRiskLevelEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CorpRiskLevelEnum;
import com.foundersc.ifte.invest.adviser.web.annotation.MetaData;
import com.foundersc.ifte.invest.adviser.web.util.AppVersionUtil;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.ifte.invest.adviser.web.util.MetaDataUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.TARGET_VERSION;
import static com.foundersc.ifte.invest.adviser.web.constants.TipConstants.*;
/**
 * 适当性匹配结果
 */
@Data
@ApiModel(value = "EligibilityCheckResultVO", description = "适当性匹配结果")
@Builder
public class EligibilityCheckResultVO {

    @ApiModelProperty("风险测评过期")
    private Boolean riskExpire;

    @ApiModelProperty("风险等级是否需要强匹配")
    private Boolean riskLevelNeedForceMatch;

    @ApiModelProperty("风险等级是否匹配")
    private Boolean riskLevelMatch;

    @ApiModelProperty("风险等级提示文案")
    private List<String> riskLevelTips;

    @ApiModelProperty("投资品种是否需要强匹配")
    private Boolean investKindNeedForceMatch;

    @ApiModelProperty("投资品种是否匹配")
    private Boolean investKindMatch;

    @ApiModelProperty("投资周期是否需要强匹配")
    private Boolean investTermNeedForceMatch;

    @ApiModelProperty("投资周期是否匹配")
    private Boolean investTermMatch;

    @ApiModelProperty("客户风险等级")
    @MetaData(clazz = CorpRiskLevelEnum.class)
    private String clientRiskLevel;

    @ApiModelProperty("组合风险等级")
    @MetaData(clazz = CombRiskLevelEnum.class)
    private String combRiskLevel;

    @ApiModelProperty("客户允许投资品种")
    @MetaData(clazz = CombInvestKindEnum.class)
    private String clientEnInvestKind;

    @ApiModelProperty("组合投资品种")
    @MetaData(clazz = CombInvestKindEnum.class)
    private String combInvestKind;

    @ApiModelProperty("客户允许投资期限")
    @MetaData(clazz = CombInvestTermEnum.class)
    private String clientEnInvestTerm;

    @ApiModelProperty("组合投资期限")
    @MetaData(clazz = CombInvestTermEnum.class)
    private String combInvestTerm;

    @ApiModelProperty("投资品种和投资期限提示文案")
    private List<String> investKindTermTips;

    @ApiModelProperty("校验是否通过")
    private Boolean checkBy;

    @ApiModelProperty("身份证是否过期")
    private Boolean idCardOverdue;

    @ApiModelProperty("是否已经进行过风险测评")
    private Boolean hasRiskLevelEval;

    @ApiModelProperty("0不允许交易 1提示风险后交易 2允许交易 3不匹配监控 4投资属性监控")
    private String combRiskLimitType;

    public Boolean getCheckBy() {
        if (Boolean.TRUE.equals(riskExpire)) {
            riskLevelMatch = false;
            // 风险测评过期，校验不通过
            return false;
        }
        if (Boolean.TRUE.equals(idCardOverdue)) {
            // 身份证过期
            return false;
        }
        if (Boolean.FALSE.equals(riskLevelMatch)) {
            // 风险等级不匹配，校验不通过
            return false;
        }
        if (Boolean.FALSE.equals(investKindMatch)) {
            // 投资品种不匹配，校验不通过
            return false;
        }
        if (Boolean.FALSE.equals(investTermMatch)) {
            // 投资期限不匹配，校验不通过
            return false;
        }
        return true;
    }

    public List<String> getRiskLevelTips() {
        // 风险等级匹配，用不到该字段，返回空
        if (Boolean.TRUE.equals(riskLevelMatch)) {
            return null;
        }
        riskLevelTips = new ArrayList<>();
        String combRiskLevelDesc = MetaDataUtil.getMetaDataDesc(CombRiskLevelEnum.class, combRiskLevel);
        String clientRiskLevelDesc = MetaDataUtil.getMetaDataDesc(CorpRiskLevelEnum.class, clientRiskLevel);

        if (AppVersionUtil.isAppVersionEnough(TARGET_VERSION, ContextHolder.getAppInfo().getVersion())) {
            riskLevelTips.add(String.format(CLIENT_RISK_LEVEL_TIP, clientRiskLevelDesc));
            riskLevelTips.add(String.format(COMB_RISK_LEVEL_TIP, combRiskLevelDesc));
        } else {
            riskLevelTips.add(String.format(RISK_LEVEL_TIP, clientRiskLevelDesc, combRiskLevelDesc));
        }
        riskLevelTips.add(Boolean.FALSE.equals(riskLevelNeedForceMatch) ? CONTINUE_BUY_TIP : RISK_LEVEL_NOT_MATCH_CANNOT_BUY_TIP);
        riskLevelTips.add(RISK_EVAL_TIP);
        return riskLevelTips;
    }

    public List<String> getInvestKindTermTips() {
        // 投资品种和投资期限都匹配时，用不到该字段，返回空
        if (Boolean.TRUE.equals(investKindMatch) && Boolean.TRUE.equals(investTermMatch)) {
            return null;
        }
        investKindTermTips = new ArrayList<>();
        String combInvestKindDesc = MetaDataUtil.getMetaDataDesc(CombInvestKindEnum.class, combInvestKind);
        String clientInvestKindDesc = MetaDataUtil.getMetaDataDesc(CombInvestKindEnum.class, clientEnInvestKind);
        String combInvestTermDesc = MetaDataUtil.getMetaDataDesc(CombInvestTermEnum.class, combInvestTerm);
        String clientInvestTermDesc = MetaDataUtil.getMetaDataDesc(CombInvestTermEnum.class, clientEnInvestTerm);
        if (AppVersionUtil.isAppVersionEnough(TARGET_VERSION, ContextHolder.getAppInfo().getVersion())) {
            investKindTermTips.add(String.format(CLIENT_INVEST_KIND_TIP, clientInvestKindDesc));
            investKindTermTips.add(String.format(CLIENT_INVEST_TERM_TIP, clientInvestTermDesc));
            investKindTermTips.add(String.format(COMB_INVEST_KIND_TIP, combInvestKindDesc));
            investKindTermTips.add(String.format(COMB_INVEST_TERM_TIP, combInvestTermDesc));
            investKindTermTips.add(String.format(INVEST_KIND_MATCH_RESULT_TIP, matchTip(investKindMatch)));
            investKindTermTips.add(String.format(INVEST_TERM_MATCH_RESULT_TIP, matchTip(investTermMatch)));
            investKindTermTips.add(Boolean.FALSE.equals(investTermNeedForceMatch) && Boolean.FALSE.equals(investKindNeedForceMatch)
                    ? CONTINUE_BUY_TIP : INVEST_KIND_TIP_NOT_MATCH_CANNOT_BUY_TIP);
        } else {
            investKindTermTips.add(String.format(CLIENT_INVEST_KIND_TERM_TIP, clientInvestKindDesc, clientInvestTermDesc));
            investKindTermTips.add(String.format(COMB_INVEST_KIND_TERM_TIP, combInvestKindDesc, combInvestTermDesc));
            investKindTermTips.add(String.format(INVEST_KIND_TERM_MATCH_RESULT_TIP, matchTip(investKindMatch), matchTip(investTermMatch)));
            investKindTermTips.add(Boolean.FALSE.equals(investTermNeedForceMatch) && Boolean.FALSE.equals(investKindNeedForceMatch)
                    ? CONTINUE_BUY_TIP : INVEST_KIND_TIP_NOT_MATCH_CANNOT_BUY_TIP);
        }

        return investKindTermTips;
    }

    private String matchTip(Boolean isMatch) {
        return Boolean.TRUE.equals(isMatch) ? "匹配" : "不匹配";
    }
}