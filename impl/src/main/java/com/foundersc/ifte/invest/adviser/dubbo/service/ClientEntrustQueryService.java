package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrust;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrustReq;

import java.util.List;

/**
 * 组合委托查询
 */
public interface ClientEntrustQueryService {

    /**
     * 客户当前委托查询
     *
     * @param simpleAccount
     * @param entrustReq
     * @return
     */
    List<CombineEntrust> queryCurrEntrusts(SimpleAccount simpleAccount, CombineEntrustReq entrustReq);

    /**
     * 客户历史委托查询
     *
     * @param simpleAccount
     * @param entrustReq
     * @return
     */
    List<CombineEntrust> queryHisEntrusts(SimpleAccount simpleAccount, CombineEntrustReq entrustReq);

    /**
     * 根据委托编号查询委托信息
     *
     * @param simpleAccount
     * @param combRequestNo
     * @param requestDate   组合申请日期
     * @return
     */
    CombineEntrust queryEntrustByRequestNo(SimpleAccount simpleAccount, String combRequestNo, Integer requestDate);
}
