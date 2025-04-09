package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombAssetInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombPositionDetail;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.ClientAssetRemoteService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ClientAssetRemoteServiceTest extends BaseTest {

    @Autowired
    private ClientAssetRemoteService clientAssetRemoteService;

    @Test
    public void testGetClientCombAssetInfo() {
        simpleAccount.setInvestorAccount(null);
        CombAssetInfo combAssetInfo = clientAssetRemoteService.getClientCombAssetInfo(simpleAccount, false);
        Assert.assertTrue(combAssetInfo.isHasInvestorAccount());
    }

    @Test
    public void testGetClientCombPositionDetail() {
        CombPositionDetail combPositionDetail = clientAssetRemoteService.getClientCombPositionDetail("8.29.0", simpleAccount, COMB_CODE);
        Assert.assertTrue(combPositionDetail != null);
    }
}
