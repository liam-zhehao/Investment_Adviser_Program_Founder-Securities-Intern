package com.foundersc.ifte.invest.adviser.dubbo.api.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrust;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrustReq;

import java.util.List;

/**
 * 组合委托相关查询
 */
public interface CombEntrustRemoteService {

    /**
     * 查询所有当前委托
     *
     * @param simpleAccount
     * @param entrustReq
     * @return
     */
    List<CombineEntrust> getAllCurrEntrust(SimpleAccount simpleAccount, CombineEntrustReq entrustReq);


    /**
     * 查询历史委托
     *
     * @param simpleAccount
     * @param entrustReq
     * @return
     */
    List<CombineEntrust> getHisEntrust(SimpleAccount simpleAccount, CombineEntrustReq entrustReq);

    /**
     * 根据申请编号查询委托信息
     *
     * @param simpleAccount
     * @param combRequestNo 申请编号
     * @param requestDate   申请时间
     * @return
     */
    CombineEntrust getEntrustByRequestNo(SimpleAccount simpleAccount, String combRequestNo, Integer requestDate) throws Exception;


}
