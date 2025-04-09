package com.foundersc.ifte.invest.adviser.dubbo.service;

import cn.hutool.core.collection.CollectionUtil;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrust;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrustReq;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombEntrustRemoteService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("qa")
public class CombEntrustRemoteServiceTest extends BaseTest {

    @Autowired
    private CombEntrustRemoteService combEntrustRemoteService;

    @Test
    public void testGetAllCurrEntrust() {
        CombineEntrustReq combineEntrustReq = new CombineEntrustReq();
        combineEntrustReq.setCombRequestNo("494098731298234368");
        combineEntrustReq.setBeginDate(20230925);
        combineEntrustReq.setEndDate(20230925);
        List<CombineEntrust> combineEntrusts = combEntrustRemoteService.getAllCurrEntrust(simpleAccount, combineEntrustReq);
        Assert.assertTrue(!CollectionUtil.isEmpty(combineEntrusts));
    }

    @Test
    public void testGetHisEntrust() {
        CombineEntrustReq combineEntrustReq = new CombineEntrustReq();
        combineEntrustReq.setBusinessType("1,2,3");
        combineEntrustReq.setBeginDate(20221101);
        combineEntrustReq.setEndDate(20221115);
        List<CombineEntrust> combineEntrusts = combEntrustRemoteService.getHisEntrust(simpleAccount, combineEntrustReq);
        Assert.assertTrue(!CollectionUtil.isEmpty(combineEntrusts));
    }


    @Test
    public void testGetEntrustByRequestNo() {
        CombineEntrust combineEntrust = null;
        try {
            combineEntrust = combEntrustRemoteService.getEntrustByRequestNo(simpleAccount, "380007532040593408", 20221114);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue(combineEntrust != null);
    }
}
