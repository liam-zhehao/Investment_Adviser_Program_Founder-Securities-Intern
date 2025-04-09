package com.foundersc.ifte.invest.adviser.dubbo.service.impl.strategy;

import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CopywritingTypeEnum;
import com.foundersc.ifc.portfolio.t2.model.v2.comb.CopywritingDTO;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.QueryCombFareArgReq;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.QueryCopywritingExtReq;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCombFareArgResp;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCopywritingExtResp;
import com.foundersc.ifc.portfolio.t2.service.v2.CombineService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CopywritingEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.TradeRule;
import com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants;
import com.foundersc.ifte.invest.adviser.dubbo.service.impl.support.TradeTimeline;
import com.foundersc.ifte.invest.adviser.dubbo.util.DataTypeUtil;
import com.foundersc.ifte.invest.adviser.dubbo.util.RatioUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.QUERY_COMB_FARE_ARG_ERROR;
import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.QUERY_COPY_WRITING_ERROR;
import static com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants.ADVISE_FEE_TEXT_1;

@Service
@Slf4j
public abstract class TradeRuleStrategy {

    @Autowired
    protected TradeTimeline tradeTimeline;

    @Autowired
    protected CombineService combineService;

    public final TradeRule assembleTradeRule(SimpleAccount simpleAccount, CombineInfo combInfo) {
        TradeRule tradeRule = initTradeRule();
        // 直接获交易规则取费率描述信息
        String autoInvestFee = getAutoInvestFee(simpleAccount, combInfo.getCombineCode());
        // 根据费用编号获取费率信息
        String manualInvestFee = getManualInvestFee(simpleAccount, combInfo.getCombChargeNo());
        // 基础信息 普通投顾组合需要
        if (!isTargetComb()) {
            assembleBasicInfo(autoInvestFee, manualInvestFee, tradeRule);
        }
        // 时间轴
        assembleTimeline(simpleAccount, combInfo, tradeRule);
        // 转入规则
        assemblePurchaseRule(tradeRule);
        // 转出规则
        assembleRedeemRule(tradeRule);
        // 调仓规则
        assembleChangePosRule(tradeRule);
        // 费用说明
        assembleFeeDesc(autoInvestFee, manualInvestFee, tradeRule);
        return tradeRule;
    }

    protected abstract void assembleTimeline(SimpleAccount simpleAccount, CombineInfo combineInfo, TradeRule tradeRule);


    protected abstract void assemblePurchaseRule(TradeRule tradeRule);

    protected abstract void assembleRedeemRule(TradeRule tradeRule);

    protected abstract void assembleChangePosRule(TradeRule tradeRule);

    protected abstract void assembleFeeDesc(String autoInvestFee, String manualInvestFee, TradeRule tradeRule);

    private String getAutoInvestFee(SimpleAccount simpleAccount, String combineCode) {
        Map<String, CopywritingDTO> type2Copywriting = getStringCopywritingDTOMap(simpleAccount, combineCode);
        return getCopywritingContent(type2Copywriting, CopywritingTypeEnum.INVEST_ADVISE_FEE_DESC);
    }


    private TradeRule initTradeRule() {
        TradeRule tradeRule = new TradeRule();
        List<TradeRule.Copywriting> otherCopywritings = new ArrayList<>();
        tradeRule.setOtherCopywritings(otherCopywritings);
        return tradeRule;
    }

    private void assembleBasicInfo(String autoInvestFee, String manualInvestFee, TradeRule tradeRule) {
        List<TradeRule.Copywriting> basicCopywritings = new ArrayList<>();
        // 投顾服务费
        TradeRule.Copywriting adviseFee = new TradeRule.Copywriting(CopywritingEnum.ADVISE_FEE);
        adviseFee.setText(autoInvestFee);
        if (StringUtils.isNotEmpty(manualInvestFee)) {
            adviseFee.setText(String.format(ADVISE_FEE_TEXT_1, manualInvestFee));
            basicCopywritings.add(adviseFee);
        }
        // 买入卖出费用
        TradeRule.Copywriting purchaseRedeemFee = new TradeRule.Copywriting(CopywritingEnum.PURCHASE_REDEEM_FEE);
        purchaseRedeemFee.setText(CommonConstants.TRADE_FEE_DESC);
        basicCopywritings.add(purchaseRedeemFee);
        tradeRule.setBasicCopywritings(basicCopywritings);
    }

    private String getManualInvestFee(SimpleAccount simpleAccount, Integer combChargeNo) {
        if (combChargeNo == null) {
            return StringUtils.EMPTY;
        }
        QueryCombFareArgResp combFareArg = queryCombFareArg(simpleAccount, combChargeNo);
        return RatioUtil.formatPercent(DataTypeUtil.strToBigDecimal(combFareArg.getRows().get(0).getFare_rate()));
    }

    /**
     * 获取文案内容
     *
     * @param type2Copywriting
     * @param copywritingTypeEnum
     * @return
     */
    private String getCopywritingContent(Map<String, CopywritingDTO> type2Copywriting, CopywritingTypeEnum copywritingTypeEnum) {
        if (!type2Copywriting.containsKey(copywritingTypeEnum.getCode())) {
            return StringUtils.EMPTY;
        }
        return type2Copywriting.get(copywritingTypeEnum.getCode()).getCopywriting_desc();
    }

    /**
     * 组合服务费参数查询
     *
     * @param simpleAccount
     * @param combChargeNo
     * @return
     */
    private QueryCombFareArgResp queryCombFareArg(SimpleAccount simpleAccount, Integer combChargeNo) {
        QueryCombFareArgReq queryCombFareArgReq = new QueryCombFareArgReq();
        queryCombFareArgReq.setCombChargeNo(String.valueOf(combChargeNo));
        BaseResult<QueryCombFareArgResp> baseResult = combineService.queryCombFareArg(simpleAccount, queryCombFareArgReq);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_COMB_FARE_ARG_ERROR + "queryCombFareArg t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BusinessException(QUERY_COMB_FARE_ARG_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error(QUERY_COMB_FARE_ARG_ERROR + "queryCombFareArg data is null");
            throw new BusinessException(QUERY_COMB_FARE_ARG_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error(QUERY_COMB_FARE_ARG_ERROR + "queryCombFareArg error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BusinessException(QUERY_COMB_FARE_ARG_ERROR);
        }
        if (CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.error(QUERY_COMB_FARE_ARG_ERROR + "queryCombFareArg combFareArgs is empty");
            throw new BusinessException(QUERY_COMB_FARE_ARG_ERROR);
        }
        return baseResult.getData();
    }

    private Map<String, CopywritingDTO> getStringCopywritingDTOMap(SimpleAccount simpleAccount, String combineCode) {
        QueryCopywritingExtReq queryCopywritingExtReq = new QueryCopywritingExtReq();
        queryCopywritingExtReq.setCombineCode(combineCode);
        BaseResult<QueryCopywritingExtResp> baseResult = combineService.queryCopywriting(simpleAccount, queryCopywritingExtReq);
        if (!baseResult.isSuccess()) {
            log.error(QUERY_COPY_WRITING_ERROR + "queryCopywriting t2 failed, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BusinessException(QUERY_COPY_WRITING_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error(QUERY_COPY_WRITING_ERROR + "queryCopywriting failed, data is null");
            throw new BusinessException(QUERY_COPY_WRITING_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error(QUERY_COPY_WRITING_ERROR + "queryCopywriting failed, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BusinessException(QUERY_COPY_WRITING_ERROR);
        }
        List<CopywritingDTO> copywritingDTOS = baseResult.getData().getRows();
        Map<String, CopywritingDTO> type2Copywriting = new HashMap<>();
        copywritingDTOS.forEach(x -> type2Copywriting.put(x.getCopywriting_type(), x));
        return type2Copywriting;
    }

    public boolean isTargetComb() {
        return false;
    }
}
