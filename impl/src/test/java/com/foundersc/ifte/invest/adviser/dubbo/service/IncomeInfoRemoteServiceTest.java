package com.foundersc.ifte.invest.adviser.dubbo.service;

import cn.hutool.core.collection.CollectionUtil;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.IncomeRatioChart;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombDailyIncome;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.IncomeInfoRemoteService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class IncomeInfoRemoteServiceTest extends BaseTest {

    @Autowired
    private IncomeInfoRemoteService incomeInfoRemoteService;

    @Test
    public void testGetDailyIncomeByMonth() {
        List<CombDailyIncome> combDailyIncomes = incomeInfoRemoteService.getDailyIncomeByMonth(simpleAccount, COMB_CODE, 20221001, 20221201);
        Assert.assertTrue(!CollectionUtil.isEmpty(combDailyIncomes));
    }

    @Test
    public void testGetCombPriceAndTradeRecord() {
        IncomeRatioChart incomeRatioChart = incomeInfoRemoteService.getCombPriceAndTradeRecord(simpleAccount, 1, COMB_CODE);
        Assert.assertTrue(incomeRatioChart != null);
    }

}
