package com.foundersc.ifte.invest.adviser.web;

import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombBusinTypeEnum;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.QueryTradeDateRuleReq;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryTradeDateRuleResp;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.T2ServiceAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author wangfuwei
 * @date 2024/7/1
 */
@SpringBootTest
@ActiveProfiles("dev")
@RunWith(SpringJUnit4ClassRunner.class)
public class T2ServiceAdapterTests extends BaseTest {
    @Autowired
    private T2ServiceAdapter t2ServiceAdapter;

    /**
     * 交易截止时间
     */
    private final static LocalTime tradeDeadTime = LocalTime.of(15, 0, 0);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueryTradeDateRuleSuccess() {
        // Arrange
        String combineCode = "A030A030020601";
        CombBusinTypeEnum combBusinType = CombBusinTypeEnum.ADD_INVEST;
        int initDate = 20240701;

        QueryTradeDateRuleReq expectedReq = new QueryTradeDateRuleReq();
        expectedReq.setCombineCode(combineCode);
        expectedReq.setInitDate(initDate);
        expectedReq.setCombBusinType(combBusinType);

        // Act
        QueryTradeDateRuleResp actualResp = t2ServiceAdapter.queryTradeDateRule(simpleAccount, combineCode, combBusinType, initDate);
        assertThat(actualResp.getPre_affirm_date(), equalTo(20240702));
    }

    @Test(expected = BizException.class)
    public void testQueryTradeDateRuleException() {
        // Arrange
        String combineCode = "A030A030020601";
        CombBusinTypeEnum combBusinType = CombBusinTypeEnum.ADD_INVEST;
        int initDate = 20240630;

        QueryTradeDateRuleReq expectedReq = new QueryTradeDateRuleReq();
        expectedReq.setCombineCode(combineCode);
        expectedReq.setInitDate(initDate);
        expectedReq.setCombBusinType(combBusinType);

        // Act
        QueryTradeDateRuleResp actualResp = t2ServiceAdapter.queryTradeDateRule(simpleAccount, combineCode, combBusinType, initDate);
    }
}
