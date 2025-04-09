package com.foundersc.ifte.invest.adviser.web;

import com.foundersc.ifte.invest.adviser.web.model.trade.PurchaseInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.RedeemInfoVO;
import com.foundersc.ifte.invest.adviser.web.service.TradeService;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author wangfuwei
 * @date 2024/7/1
 */
@SpringBootTest
@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
public class TradeServiceTests extends BaseTest {
    @Autowired
    private TradeService tradeService;

    @Before
    public void setUp() {
        ContextHolder.setSimpleAccount(simpleAccount);
        ContextHolder.setAppInfo(new ContextHolder.AppInfo("1234567890", "Ios", "8.33.0"));
    }

    @Test
    public void testGetPurchaseInfo() {
        PurchaseInfoVO purchaseInfo = tradeService.getPurchaseInfo("A005MMEFZSC");
        assertThat(purchaseInfo, Matchers.notNullValue());
    }

    @Test
    public void testGetRedeemInfo() {
        RedeemInfoVO redeemInfo = tradeService.getRedeemInfo("A005MMEFZSC");
        assertThat(redeemInfo, Matchers.notNullValue());
    }
}
