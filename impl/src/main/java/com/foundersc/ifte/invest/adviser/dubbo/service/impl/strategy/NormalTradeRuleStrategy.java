package com.foundersc.ifte.invest.adviser.dubbo.service.impl.strategy;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CopywritingEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.TradeRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants.*;

@Component("normalTradeRuleStrategy")
@Slf4j
public class NormalTradeRuleStrategy extends TradeRuleStrategy {
    @Override
    protected void assemblePurchaseRule(TradeRule tradeRule) {
        TradeRule.Copywriting purchaseRule = new TradeRule.Copywriting(CopywritingEnum.PURCHASE_RULE);
        // 转入规则-规则详情
        TradeRule.Copywriting purchaseRuleDetail = new TradeRule.Copywriting(CopywritingEnum.RULE_DETAIL);
        purchaseRuleDetail.setText(PURCHASE_RULE_DETAIL);
        purchaseRule.getContents().add(purchaseRuleDetail);
        tradeRule.getOtherCopywritings().add(purchaseRule);
    }

    @Override
    protected void assembleRedeemRule(TradeRule tradeRule) {
        // 转出规则
        TradeRule.Copywriting redeemRule = new TradeRule.Copywriting(CopywritingEnum.REDEEM_RULE);
        // 转出规则-规则详情
        TradeRule.Copywriting redeemRuleDetail = new TradeRule.Copywriting(CopywritingEnum.RULE_DETAIL);
        redeemRuleDetail.setText(REDEEM_RULE_DETAIL);
        redeemRule.getContents().add(redeemRuleDetail);
        tradeRule.getOtherCopywritings().add(redeemRule);
    }

    @Override
    protected void assembleChangePosRule(TradeRule tradeRule) {
        // 调仓规则
        TradeRule.Copywriting changePosRule = new TradeRule.Copywriting(CopywritingEnum.CHANGE_POS_RULE);
        TradeRule.Copywriting subChangePosRule = new TradeRule.Copywriting(CopywritingEnum.CHANGE_POS_RULE);
        subChangePosRule.setText(CHANGE_POS_DESC);
        changePosRule.getContents().add(subChangePosRule);
        tradeRule.getOtherCopywritings().add(changePosRule);
    }

    @Override
    protected void assembleFeeDesc(String autoInvestFee, String manualInvestFee, TradeRule tradeRule) {
        // 费用说明
        TradeRule.Copywriting feeDesc = new TradeRule.Copywriting(CopywritingEnum.FEE_DESC);
        // 费用说明-交易费用
        TradeRule.Copywriting tradeFee = new TradeRule.Copywriting(CopywritingEnum.TRADE_FEE);
        feeDesc.getContents().add(tradeFee);
        // 转入规则-交易费用-投顾服务费
        //        tradeFee.getContents().add(adviseFee);
        // 转入规则-交易费用-基金交易费用
        TradeRule.Copywriting fundTradeFee = new TradeRule.Copywriting(CopywritingEnum.FUND_TRADE_FEE);
        fundTradeFee.setText(FUND_TRADE_FEE_DESC);
        tradeFee.getContents().add(fundTradeFee);

        // 费用说明-投顾服务费
        TradeRule.Copywriting adviseFeeInFeeDesc = new TradeRule.Copywriting(CopywritingEnum.ADVISE_FEE);
        adviseFeeInFeeDesc.setText(autoInvestFee);
        if (StringUtils.isNotEmpty(manualInvestFee)) {
            adviseFeeInFeeDesc.setText(String.format(ADVISE_FEE_TEXT_2, manualInvestFee));
        }
        feeDesc.getContents().add(adviseFeeInFeeDesc);
        tradeRule.getOtherCopywritings().add(feeDesc);
    }

    @Override
    protected void assembleTimeline(SimpleAccount simpleAccount, CombineInfo combineInfo, TradeRule tradeRule) {
        LocalDateTime now = LocalDateTime.now();
        List<TradeRule.TimelineItem> buyTimeline = tradeTimeline.getBuyTimeline(simpleAccount, combineInfo.getCombineCode(), now);
        List<TradeRule.TimelineItem> redeemTimeline = tradeTimeline.getRedeemTimeline(simpleAccount, combineInfo.getCombineCode(), now);
        tradeRule.setBuyTimeline(buyTimeline);
        tradeRule.setRedeemTimeline(redeemTimeline);
    }
}
