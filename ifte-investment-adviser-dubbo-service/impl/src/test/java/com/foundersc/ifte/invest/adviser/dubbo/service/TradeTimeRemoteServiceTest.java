package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.TradeTimeCheckRes;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.TradeTimeRemoteService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author wangfuwei
 * @date 2023/10/24
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TradeTimeRemoteServiceTest extends BaseTest {
    @Autowired
    private TradeTimeRemoteService tradeTimeRemoteService;

    @Test
    public void testCheckTradeTime() {
        TradeTimeCheckRes tradeTimeCheckRes = tradeTimeRemoteService.checkTradeTime("8.29.0");
        assertThat(tradeTimeCheckRes.isSupport_7_24(), Matchers.equalTo(false));
        tradeTimeCheckRes = tradeTimeRemoteService.checkTradeTime("8.31.0");
        assertThat(tradeTimeCheckRes.isSupport_7_24(), Matchers.equalTo(true));
    }

    /**
     * 测试白名单
     */
    @Test
    public void testWhiteList() {
        SimpleAccount simpleAccount1 = new SimpleAccount();
        simpleAccount1.setClientId("82000581");
        simpleAccount1.setFundAccount("82000581");
        simpleAccount1.setInvestorAccount("82000581");
        simpleAccount1.setPassword("123123");

        TradeTimeCheckRes tradeTimeCheckRes = tradeTimeRemoteService.checkTradeTime(simpleAccount1, "8.30.0");
        assertThat(tradeTimeCheckRes.isSupport_7_24(), Matchers.equalTo(false));

        tradeTimeCheckRes = tradeTimeRemoteService.checkTradeTime(simpleAccount1, "8.31.0");
        assertThat(tradeTimeCheckRes.isSupport_7_24(), Matchers.equalTo(true));

        tradeTimeCheckRes = tradeTimeRemoteService.checkTradeTime(simpleAccount, "8.30.0");
        assertThat(tradeTimeCheckRes.isSupport_7_24(), Matchers.equalTo(false));

        tradeTimeCheckRes = tradeTimeRemoteService.checkTradeTime(simpleAccount, "8.31.0");
        assertThat(tradeTimeCheckRes.isSupport_7_24(), Matchers.equalTo(false));
    }
}
