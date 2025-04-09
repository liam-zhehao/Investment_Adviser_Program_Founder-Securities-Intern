package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.entity.InvestorAccount;

import java.util.List;

/**
 * 投顾账户相关接口
 */
public interface InvestorAccountService {
    List<InvestorAccount> queryClientInvestorAccount(SimpleAccount simpleAccount);
}
