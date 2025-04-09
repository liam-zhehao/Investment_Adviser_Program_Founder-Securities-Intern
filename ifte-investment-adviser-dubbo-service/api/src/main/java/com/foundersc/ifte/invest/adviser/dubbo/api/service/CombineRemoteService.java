package com.foundersc.ifte.invest.adviser.dubbo.api.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombinePosition;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.IncomeRatioChart;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.TradeRule;

import java.util.List;

/**
 * 组合信息相关
 *
 * @author wangfuwei
 * @date 2022/9/26
 */
public interface CombineRemoteService {
    /**
     * 获取组合基本信息
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    CombineInfo getCombInfo(SimpleAccount simpleAccount, String combineCode);

    /**
     * 查询行情走势图
     *
     * @param simpleAccount
     * @param rangeId
     * @param combineCode
     * @return
     */
    IncomeRatioChart getIncomeRationChart(SimpleAccount simpleAccount, Integer rangeId, String combineCode);

    /**
     * 根据kyc问卷获取组合列表
     *
     * @return
     */
    List<CombineInfo> getCombinfoItemByKyc(SimpleAccount simpleAccount, String orderDirection, String investOrganNo, String enTagType);

    /**
     * 查询组合持仓详情
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    CombinePosition getCombinePosition(SimpleAccount simpleAccount, String combineCode);

    /**
     * 组合交易规则
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    TradeRule getTradeRule(SimpleAccount simpleAccount, String combineCode);
}
