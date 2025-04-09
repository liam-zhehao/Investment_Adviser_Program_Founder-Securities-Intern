package com.foundersc.ifte.invest.adviser.dubbo.service.impl.strategy;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombDealStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CopywritingEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.TradeRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants.*;

@Component("targetTradeRuleStrategy")
@Slf4j
public class TargetTradeRuleStrategy extends TradeRuleStrategy {
    @Override
    protected void assembleTimeline(SimpleAccount simpleAccount, CombineInfo combineInfo, TradeRule tradeRule) {
        LocalDateTime now = LocalDateTime.now();
        if (!CombDealStatusEnum.PURCHASE.getCode().equals(combineInfo.getDealStatus())) {
            tradeRule.setBuyTimeline(new ArrayList<>());
            List<TradeRule.TimelineItem> redeemTimeline = tradeTimeline.getRedeemTimeline(simpleAccount, combineInfo.getCombineCode(), now);
            tradeRule.setRedeemTimeline(redeemTimeline);
        } else {
            List<TradeRule.TimelineItem> buyTimeline = tradeTimeline.getBuyTimeline(simpleAccount, combineInfo.getCombineCode(), now);
            List<TradeRule.TimelineItem> redeemTimeline = tradeTimeline.getRedeemTimeline(simpleAccount, combineInfo.getCombineCode(), now);
            tradeRule.setBuyTimeline(buyTimeline);
            tradeRule.setRedeemTimeline(redeemTimeline);
        }
    }

    @Override
    protected void assemblePurchaseRule(TradeRule tradeRule) {
        TradeRule.Copywriting purchaseRule = new TradeRule.Copywriting(CopywritingEnum.PURCHASE_RULE);
        // 转入规则-规则详情
        TradeRule.Copywriting purchaseRuleDetail = new TradeRule.Copywriting(CopywritingEnum.RULE_DETAIL);
        purchaseRuleDetail.setText(TARGET_PURCHASE_RULE_DETAIL);
        purchaseRule.getContents().add(purchaseRuleDetail);
        tradeRule.getOtherCopywritings().add(purchaseRule);
    }

    @Override
    protected void assembleRedeemRule(TradeRule tradeRule) {
        // 转出规则
        TradeRule.Copywriting redeemRule = new TradeRule.Copywriting(CopywritingEnum.REDEEM_RULE);
        // 转出规则-规则详情
        TradeRule.Copywriting redeemRuleDetail = new TradeRule.Copywriting(CopywritingEnum.RULE_DETAIL);
        redeemRuleDetail.setText(TARGET_REDEEM_RULE_DETAIL);
        redeemRule.getContents().add(redeemRuleDetail);
        tradeRule.getOtherCopywritings().add(redeemRule);
    }

    @Override
    protected void assembleChangePosRule(TradeRule tradeRule) {
        // 调仓规则
        TradeRule.Copywriting changePosRule = new TradeRule.Copywriting(CopywritingEnum.CHANGE_POS_RULE);
        TradeRule.Copywriting subChangePosRule = new TradeRule.Copywriting(CopywritingEnum.RULE_DETAIL);
        subChangePosRule.setText(TARGET_CHANGE_POS_DESC);
        changePosRule.getContents().add(subChangePosRule);
        tradeRule.getOtherCopywritings().add(changePosRule);
    }

    @Override
    protected void assembleFeeDesc(String autoInvestFee, String manualInvestFee, TradeRule tradeRule) {
        // 费用说明
        TradeRule.Copywriting feeDesc = new TradeRule.Copywriting(CopywritingEnum.FEE_DESC);
        // 费用说明-交易费用
        TradeRule.Copywriting tradeFee = new TradeRule.Copywriting(CopywritingEnum.TRADE_FEE);
        tradeFee.setText(FUND_TRADE_FEE_DESC);
        feeDesc.getContents().add(tradeFee);

        // 转入规则-交易费用-投顾服务费
        //        tradeFee.getContents().add(adviseFee);
        // 转入规则-交易费用-基金交易费用
//        TradeRule.Copywriting fundTradeFee = new TradeRule.Copywriting(CopywritingEnum.FUND_TRADE_FEE);
//        fundTradeFee.setText(FUND_TRADE_FEE_DESC);
//        tradeFee.getContents().add(fundTradeFee);

        // 费用说明-投顾服务费
        TradeRule.Copywriting adviseFeeInFeeDesc = new TradeRule.Copywriting(CopywritingEnum.ADVISE_FEE);
        adviseFeeInFeeDesc.setText(TARGET_ADVISE_FEE);
        feeDesc.getContents().add(adviseFeeInFeeDesc);
        tradeRule.getOtherCopywritings().add(feeDesc);
    }

    @Override
    public boolean isTargetComb() {
        return true;
    }
}
