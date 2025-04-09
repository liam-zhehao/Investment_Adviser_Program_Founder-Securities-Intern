package com.foundersc.ifte.invest.adviser.dubbo.service;

import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.KycPaperInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc.TestjourInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombineRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.KycRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class KycRemoteServiceTest extends BaseTest {

    @Autowired
    private KycRemoteService kycRemoteService;

    @Autowired
    private CombineRemoteService combineRemoteService;

    @Test
    public void testQueryInvestorAccount() {
        KycPaperInfo kycPaperInfo = kycRemoteService.queryKycPaper(simpleAccount, null);
        log.info("kycPaperInfo:{}", kycPaperInfo);
        assertThat(kycPaperInfo, notNullValue());
    }

    @Test
    public void testPostPaperAnswer() {
        // 1#10&2|2#0&1^2|
        String paperAnswer = "[{\"score\":\"0\",\"id\":\"4\",\"selected\":[\"1\"]},{\"score\":\"0\",\"id\":\"5\",\"selected\":[\"1\"]},{\"score\":\"0\",\"id\":\"6\",\"selected\":[\"1\"]}]";
        String paperVersion = "372324504866668544";
        boolean result = kycRemoteService.postPaperAnswer(simpleAccount, paperAnswer, paperVersion,"");
        assertThat(result, notNullValue());
    }

    @Test
    public void testQueryTestjour() {
        List<TestjourInfo> result = kycRemoteService.queryTestjour(simpleAccount, null);
        assertThat(result, notNullValue());
    }

    @Test
    public void testGetCombinfoItemByKyc() {
        List<CombineInfo> result = combineRemoteService.getCombinfoItemByKyc(simpleAccount, "desc", null, "0");
        log.info("testGetCombinfoItemByKyc result:{}", result);
        assertThat(result, notNullValue());
    }


}
