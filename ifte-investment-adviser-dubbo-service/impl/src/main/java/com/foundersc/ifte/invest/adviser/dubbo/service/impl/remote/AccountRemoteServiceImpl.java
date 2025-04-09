package com.foundersc.ifte.invest.adviser.dubbo.service.impl.remote;

import cn.hutool.core.bean.BeanUtil;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.account.InvestorAccountInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.AccountRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.entity.InvestorAccount;
import com.foundersc.ifte.invest.adviser.dubbo.service.InvestorAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户查询
 */
@Service("accountRemoteService")
@Slf4j
public class AccountRemoteServiceImpl implements AccountRemoteService {

    @Autowired
    private InvestorAccountService investorAccountService;

    @Override
    public List<InvestorAccountInfo> queryInvestorAccount(SimpleAccount simpleAccount) {
        List<InvestorAccount> investorAccounts = investorAccountService.queryClientInvestorAccount(simpleAccount);
        if (CollectionUtils.isEmpty(investorAccounts)) {
            // log.info("[query account] clientId {} does not have investor account", simpleAccount.getClientId());
            return null;
        }
        List<InvestorAccountInfo> accountInfos = new ArrayList<>(investorAccounts.size());
        investorAccounts.stream().forEach(account -> {
            InvestorAccountInfo accountInfo = new InvestorAccountInfo();
            BeanUtil.copyProperties(account, accountInfo);
            accountInfos.add(accountInfo);
        });

        return accountInfos;
    }
}
