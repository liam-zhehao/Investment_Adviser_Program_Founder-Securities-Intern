package com.foundersc.ifte.invest.adviser.dubbo.api.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.IncomeRatioChart;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombDailyIncome;

import java.util.List;

/**
 * 收益信息查询接口，包括行情曲线，收益日历等
 */
public interface IncomeInfoRemoteService {

    /**
     * 按月查询用户每日收益信息
     *
     * @param simpleAccount
     * @param startDate
     * @param endDate
     * @return
     */
    List<CombDailyIncome> getDailyIncomeByMonth(SimpleAccount simpleAccount, String combineCode, int startDate, int endDate);


    /**
     * 查询组合历史行情和交易记录
     *
     * @param simpleAccount
     * @param btnId
     * @param combineCode
     */
    IncomeRatioChart getCombPriceAndTradeRecord(SimpleAccount simpleAccount, Integer btnId, String combineCode);

}
