package com.foundersc.ifte.invest.adviser.web.service.impl.strategy.assemble;

import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCombFareArgResp;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.web.constants.CommonConstants;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.T2ServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.util.CombineTagUtil;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.ifte.invest.adviser.web.util.DataUtil;
import com.foundersc.ifte.invest.adviser.web.util.RatioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 组装组合信息
 */
@Service
@Slf4j
public abstract class CombAssemble {

    @Autowired
    private T2ServiceAdapter t2ServiceAdapter;

    @Value("${url.purchase}")
    private String purchaseUrl;

    /**
     * 组装流程
     */
    public final void assembleComb(CombineInfoVO resultVO, CombineInfo combInfo) {
        // 公共信息
        assembleCommonInfo(resultVO, combInfo);
        // 基础信息
        assembleBaseInfo(resultVO, combInfo);
        // 行情信息
        assembleMarketInfo(resultVO, combInfo);
        // 交易信息
        assembleTradeInfo(resultVO, combInfo);
        // 声明信息
        assembleStatementInfo(resultVO, combInfo);
        // 不同组合类型的特有信息
        assembleSpecificInfo(resultVO, combInfo);
    }


    private void assembleCommonInfo(CombineInfoVO resultVO, CombineInfo combInfo) {
        // 购买提示
        if (resultVO.getCombChargeNo() != null) {
            QueryCombFareArgResp combFareArg = t2ServiceAdapter.queryCombFareArg(ContextHolder.getSimpleAccount(), resultVO.getCombChargeNo());
            BigDecimal adviseFee = DataUtil.strToBigDecimal(combFareArg.getRows().get(0).getFare_rate());
            resultVO.setAdviseFee(RatioUtil.formatPercent(DataUtil.strToBigDecimal(combFareArg.getRows().get(0).getFare_rate())));
            resultVO.setCalAdviseFee(adviseFee);
        } else {
            log.warn("combChargeNo is null, set adviseFee blank");
            resultVO.setAdviseFee(CommonConstants.BLANK_STR);
        }
        resultVO.setPurchaseUrl(String.format(purchaseUrl, resultVO.getCombineCode()));
    }


    protected abstract void assembleBaseInfo(CombineInfoVO resultVO, CombineInfo combInfo);

    protected abstract void assembleMarketInfo(CombineInfoVO resultVO, CombineInfo combInfo);

    protected abstract void assembleTradeInfo(CombineInfoVO resultVO, CombineInfo combInfo);

    protected abstract void assembleStatementInfo(CombineInfoVO resultVO, CombineInfo combInfo);

    public abstract String getCombType();

    public void assembleSpecificInfo(CombineInfoVO resultVO, CombineInfo combInfo) {

    }

}
