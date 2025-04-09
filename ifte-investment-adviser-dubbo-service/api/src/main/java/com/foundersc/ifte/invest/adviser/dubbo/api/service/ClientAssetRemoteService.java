package com.foundersc.ifte.invest.adviser.dubbo.api.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.ClientCombAsset;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombAssetInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombFetchAsset;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombPositionDetail;

/**
 * 投顾组合持仓相关服务
 */
public interface ClientAssetRemoteService {

    /**
     * 查询用户持仓总资产和组合资产列表
     *
     * @param simpleAccount
     * @param isTotalAssetOnly 是否只查询总资产
     * @return
     */
    CombAssetInfo getClientCombAssetInfo(SimpleAccount simpleAccount, boolean isTotalAssetOnly);

    /**
     * 单个组合持仓详情查询
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    CombPositionDetail getClientCombPositionDetail(SimpleAccount simpleAccount, String combineCode);

    /**
     * 单个组合持仓详情查询
     *
     * @param appVersion
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    CombPositionDetail getClientCombPositionDetail(String appVersion, SimpleAccount simpleAccount, String combineCode);


    /**
     * 查询单个组合资产信息
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    ClientCombAsset getSingleCombAsset(SimpleAccount simpleAccount, String combineCode);
    /**
     * 客户组合可取资产查询
     * @param combineCode
     */
    CombFetchAsset getClientCombFetchAsset(SimpleAccount simpleAccount, String combineCode);
}
