package com.foundersc.ifte.invest.adviser.dubbo.api.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombStrategyInfo;

import java.util.List;

/**
 * 组合策略信息接口
 */
public interface CombStrategyRemoteService {

    /**
     * 查询进行中的策略组合
     * @param simpleAccount
     * @param dealStatus
     * @return
     */
    List<CombStrategyInfo> getCurrCombStrategyList(SimpleAccount simpleAccount, String combineCode,String dealStatus);

    /**
     * 所有指定的组合列表接口
     * @param simpleAccount
     * @param dealStatuses
     * @param beginDate 展示初始日期 不传取近一年
     * @return
     */
    List<CombStrategyInfo> getCombStrategyList(SimpleAccount simpleAccount, List<String> dealStatuses, Integer beginDate);
}
