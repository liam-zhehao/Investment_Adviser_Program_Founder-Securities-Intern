package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.CombAgreement;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.ClientCombAsset;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.ClientTotalAsset;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombFetchAsset;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombShareItem;

import java.util.List;

public interface ClientAssetQueryService {

    /**
     * 查询投顾签约信息
     *
     * @param simpleAccount
     */
    List<CombAgreement> getCombAgreementInfo(SimpleAccount simpleAccount);

    /**
     * 客户总资产查询
     *
     * @param simpleAccount
     * @return
     */
    ClientTotalAsset getClientTotalAsset(SimpleAccount simpleAccount);


    /**
     * 客户组合资产查询
     *
     * @param simpleAccount
     * @param combineCode   组合代码
     * @return
     */
    List<ClientCombAsset> getClientCombAsset(SimpleAccount simpleAccount, String combineCode);


    /**
     * 组合持仓成分信息查询
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    List<CombShareItem> getCombPositionShares(SimpleAccount simpleAccount, String combineCode);

    /**
     * 组合持仓可取资产查询
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    CombFetchAsset getCombFetchAsset(SimpleAccount simpleAccount, String combineCode);
}
