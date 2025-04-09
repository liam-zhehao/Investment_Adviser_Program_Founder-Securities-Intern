package com.foundersc.ifte.invest.adviser.dubbo.service;

import cn.hutool.core.collection.CollectionUtil;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.account.InvestorAccountInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.AccountRemoteService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class AccountRemoteServiceTest extends BaseTest {

    @Autowired
    private AccountRemoteService accountRemoteService;

    @Test
    public void testQueryInvestorAccount() {
        simpleAccount.setInvestorAccount(null);
        List<InvestorAccountInfo> investorAccountInfos = accountRemoteService.queryInvestorAccount(simpleAccount);
        Assert.assertTrue(CollectionUtil.isNotEmpty(investorAccountInfos));
    }

}
