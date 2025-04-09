package com.foundersc.ifte.invest.adviser.dubbo.service.impl.remote;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrust;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrustReq;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombEntrustRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.service.ClientEntrustQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("combEntrustRemoteService")
public class CombEntrustRemoteServiceImpl implements CombEntrustRemoteService {

    @Autowired
    private ClientEntrustQueryService clientEntrustQueryService;


    @Override
    public List<CombineEntrust> getAllCurrEntrust(SimpleAccount simpleAccount, CombineEntrustReq entrustReq) {
        List<CombineEntrust> combineEntrusts = clientEntrustQueryService.queryCurrEntrusts(simpleAccount, entrustReq);
        return combineEntrusts;
    }

    @Override
    public List<CombineEntrust> getHisEntrust(SimpleAccount simpleAccount, CombineEntrustReq entrustReq) {
        List<CombineEntrust> combineEntrusts = clientEntrustQueryService.queryHisEntrusts(simpleAccount, entrustReq);
        return combineEntrusts;
    }

    @Override
    public CombineEntrust getEntrustByRequestNo(SimpleAccount simpleAccount, String combRequestNo, Integer requestDate) throws Exception {
        return clientEntrustQueryService.queryEntrustByRequestNo(simpleAccount, combRequestNo, requestDate);
    }
}
