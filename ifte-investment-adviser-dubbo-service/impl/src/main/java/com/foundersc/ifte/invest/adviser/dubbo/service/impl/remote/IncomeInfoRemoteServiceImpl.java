package com.foundersc.ifte.invest.adviser.dubbo.service.impl.remote;

import cn.hutool.core.util.StrUtil;
import com.foundersc.ifc.portfolio.t2.request.CombHisAssetReq;
import com.foundersc.ifc.portfolio.t2.response.invest.CombHisAssetDTO;
import com.foundersc.ifc.portfolio.t2.response.invest.CombHisAssetResp;
import com.foundersc.ifc.portfolio.t2.service.ClientAssetService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombBusTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombEntrustStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.TradeDirectionEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.IncomeRatioChart;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombDailyIncome;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrust;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrustReq;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombineRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.IncomeInfoRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants;
import com.foundersc.ifte.invest.adviser.dubbo.enums.QueryTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.enums.SortEnum;
import com.foundersc.ifte.invest.adviser.dubbo.service.ClientEntrustQueryService;
import com.foundersc.ifte.invest.adviser.dubbo.util.ObjectCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombBusTypeEnum.*;
import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.QUERY_COMB_HIS_ASSET_ERROR;

/**
 * 收益信息查询
 */
@Service("incomeInfoRemoteService")
@Slf4j
public class IncomeInfoRemoteServiceImpl implements IncomeInfoRemoteService {

    @Autowired
    private ClientAssetService clientAssetService;

    @Autowired
    private CombineRemoteService combineRemoteService;

    @Autowired
    private ClientEntrustQueryService clientEntrustQueryService;

    @Override
    public List<CombDailyIncome> getDailyIncomeByMonth(SimpleAccount simpleAccount, String combineCode, int startDate, int endDate) {
        if (StrUtil.isEmpty(combineCode)) {
            return null;
        }
        log.info("[daily income] clientId {} begin to query combineCode {} daily income", simpleAccount.getClientId(), combineCode);
        CombHisAssetReq combHisAssetReq = new CombHisAssetReq();
        combHisAssetReq.setBeginDate(startDate);
        combHisAssetReq.setEndDate(endDate);
        combHisAssetReq.setCombineCode(combineCode);
        combHisAssetReq.setQueryType(QueryTypeEnum.NAV_DATE.getId());
        combHisAssetReq.setQueryFlag(SortEnum.ASC.getId());
        BaseResult<CombHisAssetResp> combHisAssetResult = clientAssetService.getCombHisAsset(simpleAccount, combHisAssetReq);
        if (!combHisAssetResult.isSuccess()) {
            log.error(QUERY_COMB_HIS_ASSET_ERROR + "[daily income] clientId {} query daily income error", simpleAccount.getClientId(), combHisAssetResult.getErrorMsg());
            throw new BusinessException(QUERY_COMB_HIS_ASSET_ERROR);
        }
        if (combHisAssetResult.getData() == null || CollectionUtils.isEmpty(combHisAssetResult.getData().getRows())) {
            log.info("[daily income] clientId {} query daily income size is zero.", simpleAccount.getClientId());
            return null;
        }
        List<CombHisAssetDTO> combHisAssetDTOs = combHisAssetResult.getData().getRows();
        CombineInfo combInfo = combineRemoteService.getCombInfo(simpleAccount, combineCode);
        if (combInfo.isTargetComb()) {
            if (combInfo.getOperationalStartDate() == null) {
                log.info("operationalStartDate is null, set combHisAssetDTOs empty");
                combHisAssetDTOs = new ArrayList<>();
            } else {
                // 如果是目标盈组合，则过滤掉operationalStartDate之前的记录
                combHisAssetDTOs = combHisAssetDTOs.stream().filter(x -> {
                    Integer incomeDate = getIntDate(x.getCombi_income_date());
                    return incomeDate != null && incomeDate >= combInfo.getOperationalStartDate();
                }).collect(Collectors.toList());
            }
        }
        List<CombDailyIncome> combDailyIncomes = new ArrayList<>(combHisAssetDTOs.size());
        ObjectCopyUtil.copyCombHisAsset(combHisAssetDTOs, combDailyIncomes);
        log.info("[daily income] client {} query daily income size {}", simpleAccount.getClientId(), combDailyIncomes.size());
        return combDailyIncomes;
    }

    /**
     * date由string转为int
     *
     * @param strDate
     * @return
     */
    private Integer getIntDate(String strDate) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }
        try {
            return Integer.parseInt(strDate);
        } catch (NumberFormatException e) {
            log.error("parse date error, strDate={}", strDate, e);
            return null;
        }
    }

    @Override
    public IncomeRatioChart getCombPriceAndTradeRecord(SimpleAccount simpleAccount, Integer btnId, String combineCode) {
        IncomeRatioChart incomeRatioChart = combineRemoteService.getIncomeRationChart(simpleAccount, btnId, combineCode);
        if (incomeRatioChart == null || incomeRatioChart.getCombineLine() == null
                || CollectionUtils.isEmpty(incomeRatioChart.getCombineLine().getIncomeRatios())) {
            log.info("[comb price] clientId {} query combineCode {} btnId {} empty.", simpleAccount.getClientId(), combineCode, btnId);
            return null;
        }
        IncomeRatioChart.IncomeRatioLine incomeRatioLine = incomeRatioChart.getCombineLine();
        List<IncomeRatioChart.IncomeRatio> incomeRatios = incomeRatioLine.getIncomeRatios();
        List<CombineEntrust> hisCombEntrusts = getHisTradeRecord(simpleAccount, combineCode, incomeRatios);
        if (CollectionUtils.isEmpty(hisCombEntrusts)) {
            log.info("[comb price] clientId {} his entrust size is zero.", simpleAccount.getClientId());
            return incomeRatioChart;
        }
        Map<Integer, Integer> dailyTradeRecords = new HashMap<>();
        for (CombineEntrust entrust : hisCombEntrusts) {
            if (!CombEntrustStatusEnum.AFFIRM_SUCCESS.getCode().equals(entrust.getCombRequestStatus())) {
                continue;
            }
            String businessType = entrust.getCombBusinessType();
            if (COMB_REDEEM.getCode().equals(businessType) || COMB_TERMINATE.getCode().equals(businessType)) {
                dailyTradeRecords.put(entrust.getInitDate(), TradeDirectionEnum.REDEEM.getCode());
            }
            if ((COMB_SIGN.getCode().equals(businessType) || COMB_BUY.getCode().equals(businessType))
                    && dailyTradeRecords.get(entrust.getInitDate()) == null) {
                dailyTradeRecords.put(entrust.getInitDate(), TradeDirectionEnum.PURCHASE.getCode());
            }
        }
        incomeRatios.stream().forEach(incomeRatio -> {
            incomeRatio.setDirection(dailyTradeRecords.containsKey(incomeRatio.getDate())
                    ? dailyTradeRecords.get(incomeRatio.getDate()) : TradeDirectionEnum.NONE.getCode());
        });

        return incomeRatioChart;
    }

    private List<CombineEntrust> getHisTradeRecord(SimpleAccount simpleAccount, String combineCode, List<IncomeRatioChart.IncomeRatio> incomeRatios) {
        IncomeRatioChart.IncomeRatio beginIncomeRatio = incomeRatios.get(0);
        IncomeRatioChart.IncomeRatio endIncomeRatio = incomeRatios.get(incomeRatios.size() - 1);
        CombineEntrustReq entrustReq = new CombineEntrustReq();
        entrustReq.setCombineCode(combineCode);
        entrustReq.setBeginDate(beginIncomeRatio.getDate());
        entrustReq.setEndDate(endIncomeRatio.getDate());
        String businessType = StrUtil.join(CommonConstants.SPLIT_COMMA, CombBusTypeEnum.tradeRecordBusTypes);
        entrustReq.setBusinessType(businessType);
        return clientEntrustQueryService.queryHisEntrusts(simpleAccount, entrustReq);
    }
}
