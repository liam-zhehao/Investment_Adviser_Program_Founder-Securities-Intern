package com.foundersc.ifte.invest.adviser.web.service;

import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombinePositionVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.IncomeRatioChartVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.TradeRuleVO;

import java.util.List;

/**
 * @author wangfuwei
 * @date 2022/9/28
 */
public interface CombInfoService {
    /**
     * 组合信息
     *
     * @param combineCode
     * @return
     */
    CombineInfoVO info(String combineCode, int initDate);

    /**
     * 组合行情走势图
     *
     * @param combineCode
     * @param rangeId
     * @param initDate
     * @return
     */
    IncomeRatioChartVO trend(String combineCode, int rangeId, int initDate);

    /**
     * 持仓详情
     *
     * @param combineCode
     * @param initDate
     * @return
     */
    CombinePositionVO position(String combineCode, int initDate);

    /**
     * 交易规则
     *
     * @param combineCode
     * @param initDate
     * @return
     */
    TradeRuleVO tradeRule(String combineCode, int initDate);

    /**
     * 转入文案信息
     *
     * @param combineCode
     * @param initDate
     * @return
     */
    List<String> getPurchaseCopywritings(String combineCode, int initDate);

    /**
     * 转出文案信息
     *
     * @param combineCode
     * @param initDate
     * @return
     */
    List<String> getRedeemCopywritings(String combineCode, int initDate);
}
