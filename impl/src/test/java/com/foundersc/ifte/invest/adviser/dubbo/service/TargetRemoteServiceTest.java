package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombStrategyInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombAssetInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombPositionDetail;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.ClientAssetRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombStrategyRemoteService;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TargetRemoteServiceTest extends BaseTest {

    @Autowired
    private CombStrategyRemoteService combStrategyRemoteService;

    @Test
    public void testGetCombStrategyList() {
        simpleAccount.setInvestorAccount(null);
        List<CombStrategyInfo> combStrategyInfos = combStrategyRemoteService.getCombStrategyList(simpleAccount, Lists.newArrayList("4"),null);
        Assert.assertTrue(!CollectionUtils.isEmpty(combStrategyInfos));
    }


}
