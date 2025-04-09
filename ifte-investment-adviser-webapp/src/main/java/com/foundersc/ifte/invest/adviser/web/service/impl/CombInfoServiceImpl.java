package com.foundersc.ifte.invest.adviser.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.foundersc.ifc.common.util.StringUtils;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombBusinTypeEnum;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCombFareArgResp;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryTradeDateRuleResp;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombProfitTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombinePosition;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.IncomeRatioChart;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.TradeRule;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombineRemoteService;
import com.foundersc.ifte.invest.adviser.web.constants.TipConstants;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombinePositionVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.IncomeRatioChartVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.TradeRuleVO;
import com.foundersc.ifte.invest.adviser.web.service.CombInfoService;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.CommonFactory;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.assemble.CombAssemble;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.T2ServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.web.constants.RedisConstants.*;
import static com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum.COMB_PROFIT_TYPE_NOT_SUPPORT;

/**
 * @author wangfuwei
 * @date 2022/9/28
 */
@Service("combInfoService")
@Slf4j
public class CombInfoServiceImpl implements CombInfoService {
    @Autowired
    private CombineRemoteService combineRemoteService;

    @Autowired
    private T2ServiceAdapter t2ServiceAdapter;

    @Autowired
    private CombInfoService combInfoService;


    @Override
    @Cacheable(cacheNames = COMB_INFO, key = "#combineCode + ':' + #initDate", unless = "#result == null")
    public CombineInfoVO info(String combineCode, int initDate) {
        CombineInfo combInfo = combineRemoteService.getCombInfo(ContextHolder.getSimpleAccount(), combineCode);
        if (combInfo.getSetUpDate() == null) {
            throw new BizException(BizErrorCodeEnum.COMB_SETUP_DATE_ERROR);
        }

        if (StringUtils.isEmpty(combInfo.getCombProfitType()) ||
                !CombProfitTypeEnum.allCombTypes.contains(combInfo.getCombProfitType())) {
            throw new BizException(BizErrorCodeEnum.COMB_PROFIT_TYPE_ERROR);
        }
        CombineInfoVO result = getCombineInfoVO(combInfo);
        CombAssemble combAssemble = CommonFactory.getAssembleTemplate(CombProfitTypeEnum.parseByTypeId(combInfo.getCombProfitType()));
        if (combAssemble == null) {
            log.error("comb info {} current profit type {} not support", combInfo.getCombineCode(), combInfo.getCombProfitType());
            throw new BizException(COMB_PROFIT_TYPE_NOT_SUPPORT);
        }
        combAssemble.assembleComb(result, combInfo);
        return result;
    }


    @Override
    @Cacheable(cacheNames = COMB_TREND, key = "#combineCode + ':' + #rangeId + ':' + #initDate", unless = "#result == null")
    public IncomeRatioChartVO trend(String combineCode, int rangeId, int initDate) {
        IncomeRatioChart incomeRationChart = combineRemoteService.getIncomeRationChart(ContextHolder.getSimpleAccount(), rangeId, combineCode);
        return JSONObject.parseObject(JSONObject.toJSONString(incomeRationChart), IncomeRatioChartVO.class);
    }

    @Override
    @Cacheable(cacheNames = COMB_POS, key = "#combineCode + ':' + #initDate", unless = "#result == null")
    public CombinePositionVO position(String combineCode, int initDate) {
        CombinePosition combinePosition = combineRemoteService.getCombinePosition(ContextHolder.getSimpleAccount(), combineCode);
        return JSONObject.parseObject(JSONObject.toJSONString(combinePosition), CombinePositionVO.class);
    }

    @Override
    @Cacheable(cacheNames = COMB_RULE, key = "#combineCode + ':' + #initDate", unless = "#result == null")
    public TradeRuleVO tradeRule(String combineCode, int initDate) {
        TradeRule tradeRule = combineRemoteService.getTradeRule(ContextHolder.getSimpleAccount(), combineCode);
        return JSONObject.parseObject(JSONObject.toJSONString(tradeRule), TradeRuleVO.class);
    }

    @Override
    @Cacheable(cacheNames = COMB_PURCHASE_COPYWRITING, key = "#combineCode + ':' + #initDate", unless = "#result == null")
    public List<String> getPurchaseCopywritings(String combineCode, int initDate) {
        CombineInfoVO combineInfo = combInfoService.info(combineCode, initDate);
        List<String> result = new ArrayList<>();
        // 预计确认日期
        QueryTradeDateRuleResp tradeDateRule = t2ServiceAdapter.queryTradeDateRule(ContextHolder.getSimpleAccount(), combineCode, CombBusinTypeEnum.ADD_INVEST, initDate);
        Integer affirmDate = tradeDateRule.getPre_affirm_date();

        result.add(String.format(TipConstants.PURCHASE_AFFIRM_TIP, DateUtil.formatChineseMonthDay(affirmDate), DateUtil.toChineseDate(affirmDate)));
        // 转入费率
        if (combineInfo.getCombChargeNo() == null) {
            log.warn("combChargeNo is null");
            return result;
        }
        QueryCombFareArgResp combFareArg = t2ServiceAdapter.queryCombFareArg(ContextHolder.getSimpleAccount(), combineInfo.getCombChargeNo());
        result.add(String.format(TipConstants.PURCHASE_RATIO_TIP, RatioUtil.formatPercent(DataUtil.strToBigDecimal(combFareArg.getRows().get(0).getFare_rate()))));
        return result;
    }

    @Override
    @Cacheable(cacheNames = COMB_REDEEM_COPYWRITING, key = "#combineCode + ':' + #initDate", unless = "#result == null")
    public List<String> getRedeemCopywritings(String combineCode, int initDate) {
        CombineInfoVO combineInfo = combInfoService.info(combineCode, initDate);
        List<String> result = new ArrayList<>();
        // 预计确认日期
        QueryTradeDateRuleResp tradeDateRule = t2ServiceAdapter.queryTradeDateRule(ContextHolder.getSimpleAccount(), combineCode, CombBusinTypeEnum.REDUCE_INVEST, initDate);
        Integer arriveDate = tradeDateRule.getPre_arrive_date();
        result.add(String.format(TipConstants.REDEEM_AFFIRM_TIP, DateUtil.formatChineseMonthDay(initDate), DateUtil.toChineseDate(initDate),
                DateUtil.formatChineseMonthDay(arriveDate), DateUtil.toChineseDate(arriveDate)));
        // 转出费率
        if (combineInfo.getCombChargeNo() == null) {
            log.warn("combChargeNo is null");
            return result;
        }
        result.add(TipConstants.REDEEM_RATIO_TIP);
//        QueryCombFareArgResp combFareArg = t2ServiceAdapter.queryCombFareArg(ContextHolder.getSimpleAccount(), combineInfo.getCombChargeNo());
//        result.add(String.format(TipConstants.REDEEM_RATIO_TIP, RatioUtil.formatPercent(DataUtil.strToBigDecimal(combFareArg.getRows().get(0).getFare_rate()))));
        return result;
    }

    private CombineInfoVO getCombineInfoVO(CombineInfo combInfo) {
        CombineInfoVO result = new CombineInfoVO();
        BeanUtil.copyProperties(combInfo, result);
        return result;
    }
}
