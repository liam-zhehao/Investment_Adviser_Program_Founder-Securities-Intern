package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.alibaba.fastjson.JSONObject;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombTrendRangeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombinePosition;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.IncomeRatioChart;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.TradeRule;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombineRemoteService;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author wangfuwei
 * @date 2022/10/18
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = "qa")
public class CombineRemoteServiceTest extends BaseTest {
    @Autowired
    private CombineRemoteService combineRemoteService;

    @Test
    public void testGetCombInfo() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            CombineInfo combInfo = combineRemoteService.getCombInfo(simpleAccount, COMB_CODE);
            assertThat(combInfo, CoreMatchers.notNullValue());
            TimeUnit.SECONDS.sleep(20);
        }
    }

    @Test
    public void testGetIncomeRationChart() {
        IncomeRatioChart incomeRationChart = combineRemoteService.getIncomeRationChart(simpleAccount, CombTrendRangeEnum.ONE_MONTH.getRangeId(), COMB_CODE);
        assertThat(incomeRationChart, CoreMatchers.notNullValue());
    }

    @Test
    public void testGetCombinePosition() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            CombinePosition combinePosition = combineRemoteService.getCombinePosition(simpleAccount, COMB_CODE);
            System.out.println(JSONObject.toJSONString(combinePosition.getTypeRatios()));
            assertThat(combinePosition, CoreMatchers.notNullValue());
            TimeUnit.SECONDS.sleep(20);
        }
    }

    @Test
    public void testGetTradeRule() {
        TradeRule tradeRule = combineRemoteService.getTradeRule(simpleAccount, COMB_CODE);
        assertThat(tradeRule, CoreMatchers.notNullValue());
    }
}
