package com.foundersc.ifte.invest.adviser.dubbo.api.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.account.InvestorAccountInfo;

import java.util.List;

/**
 * 账户查询
 */
public interface AccountRemoteService {

    /**
     * 根据资产账户查询投顾账户
     *
     * @param simpleAccount
     * @return
     */
    List<InvestorAccountInfo> queryInvestorAccount(SimpleAccount simpleAccount);
}
