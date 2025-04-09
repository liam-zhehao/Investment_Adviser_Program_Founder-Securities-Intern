package com.foundersc.ifte.invest.adviser.web.service.impl;

import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombKycAgreeTypeEnum;
import com.foundersc.ifc.portfolio.t2.model.v2.comb.CombTagDTO;
import com.foundersc.ifc.portfolio.t2.request.v2.kyc.TargetKycAnswerReq;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCombTagResp;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombDealStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombStrategyInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombStrategyRemoteService;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineStrategyVO;
import com.foundersc.ifte.invest.adviser.web.model.kyc.TargetKycInfo;
import com.foundersc.ifte.invest.adviser.web.service.CombInfoService;
import com.foundersc.ifte.invest.adviser.web.service.TargetCombineService;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.T2ServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.util.BeanUtil;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.itc.product.utils.DateUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombDealStatusEnum.*;
import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.*;
import static com.foundersc.ifte.invest.adviser.web.constants.RedisConstants.*;
import static com.foundersc.ifte.invest.adviser.web.constants.TipConstants.TARGET_BUY_END_TIP;
import static com.foundersc.ifte.invest.adviser.web.constants.TipConstants.TARGET_KYC_TITLE;
import static com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum.COMB_TAG_QUERY_ERROR;
import static com.foundersc.ifte.invest.adviser.web.util.TargetDateUtil.assembleDate;

@Service("targetCombineService")
@Slf4j
public class TargetCombineServiceImpl implements TargetCombineService {

    @Autowired
    private CombStrategyRemoteService combStrategyRemoteService;

    @Autowired
    private CombInfoService combInfoService;

    @Autowired
    private T2ServiceAdapter t2ServiceAdapter;

    public static final String FILTER_TEXT = "测试";


    @Override
    @Cacheable(cacheNames = TARGET_CURR_LIST, key = "#initDate", unless = "#result == null")
    public List<CombineStrategyVO> getOnSaleCombineList(int initDate) {
        List<CombStrategyInfo> combStrategyInfos = combStrategyRemoteService.getCurrCombStrategyList(ContextHolder.getSimpleAccount(), null, PURCHASE.getCode());
        if (CollectionUtils.isEmpty(combStrategyInfos)) {
            return null;
        }
        return assembleCombineStrategyVO(combStrategyInfos);

    }


    @Override
    @Cacheable(cacheNames = TARGET_HIS_LIST, key = "#initDate", unless = "#result == null")
    public List<CombineStrategyVO> getHisCombineList(int initDate) {
        List<CombStrategyInfo> operatingCombs = combStrategyRemoteService.getCombStrategyList(ContextHolder.getSimpleAccount(), CombDealStatusEnum.operatingStatus, null);
        List<CombStrategyInfo> stopProfitCombs = combStrategyRemoteService.getCombStrategyList(ContextHolder.getSimpleAccount(), Lists.newArrayList(PROFIT_STOPPED.getCode()), null);
        List<CombStrategyInfo> expiredCombs = combStrategyRemoteService.getCombStrategyList(ContextHolder.getSimpleAccount(), Lists.newArrayList(TERMINATED.getCode()), null);
        List<CombineStrategyVO> combineStrategyVOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(operatingCombs)) {
            combineStrategyVOs.addAll(assembleCombineStrategyVO(operatingCombs));
        }
        if (!CollectionUtils.isEmpty(stopProfitCombs)) {
            combineStrategyVOs.addAll(assembleCombineStrategyVO(stopProfitCombs));
        }
        if (!CollectionUtils.isEmpty(expiredCombs)) {
            combineStrategyVOs.addAll(assembleCombineStrategyVO(expiredCombs));
        }
        return combineStrategyVOs;
    }

    @Override
    @Cacheable(cacheNames = TARGET_KYC_INFO, key = "#combineCode", unless = "#result == null")
    public TargetKycInfo queryTargetKycInfo(String combineCode) {
        QueryCombTagResp combTagResp = t2ServiceAdapter.getCombTagInfo(ContextHolder.getSimpleAccount(), combineCode);
        TargetKycInfo targetKycInfo = new TargetKycInfo();
        targetKycInfo.setKycTitle(TARGET_KYC_TITLE);
        if (CollectionUtils.isEmpty(combTagResp.getRows())) {
            log.error("queryTargetKycInfo comb {} no kyc tags.", combineCode);
            throw new BizException(COMB_TAG_QUERY_ERROR);
        }
        List<CombTagDTO> combTagDTOs = combTagResp.getRows();
        combTagDTOs = combTagDTOs.stream().filter(combTagDTO -> combTagDTO.getTag_name().contains(MBYKYC)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(combTagDTOs)) {
            log.error("queryTargetKycInfo comb {} no kyc prefix tags.", combineCode);
            throw new BizException(COMB_TAG_QUERY_ERROR);
        }
        StringBuilder detail = new StringBuilder();
        int lastTagIndex = combTagDTOs.size() - 1;
        for (int i = 0; i <= lastTagIndex; i++) {
            String oriTagName = combTagDTOs.get(i).getTag_name();
            oriTagName = oriTagName.substring(oriTagName.indexOf(MBYKYC_SPLIT) + 1);
            detail.append(oriTagName);
            if (i == lastTagIndex) {
                detail.append(PERIOD);
            } else {
                detail.append(COMMA);
            }
        }
        targetKycInfo.setKycDetail(detail.toString());
        return targetKycInfo;
    }

    @Override
    public boolean commitTargetKyc(String combineCode) {
        log.info("commitTargetKyc client {} commit combineCode {}", ContextHolder.getSimpleAccount().getClientId(), combineCode);
        QueryCombTagResp tagResp = t2ServiceAdapter.getCombTagInfo(ContextHolder.getSimpleAccount(), combineCode);
        Pair<String, String> agreePair = assembleAgreeContent(tagResp.getRows());
        TargetKycAnswerReq kycAnswerReq = new TargetKycAnswerReq();
        kycAnswerReq.setCombineCode(combineCode);
        kycAnswerReq.setAgreeType(CombKycAgreeTypeEnum.TARGET.getTypeCode());
        kycAnswerReq.setFztgAgreementId(agreePair.getLeft());
        kycAnswerReq.setAgreementContent(agreePair.getRight());
        return t2ServiceAdapter.signTargetKycPaper(ContextHolder.getSimpleAccount(), kycAnswerReq);
    }


    private List<CombineStrategyVO> assembleCombineStrategyVO(List<CombStrategyInfo> combStrategyInfos) {
        Date currentDate = new Date();
        Integer integerDate = DateUtils.getIntegerDate(currentDate);
        List<CombineStrategyVO> combineStrategyVOS = new ArrayList<>(combStrategyInfos.size());
        for (CombStrategyInfo combStrategyInfo : combStrategyInfos) {
            CombDealStatusEnum statusEnum = CombDealStatusEnum.parseByStatusCode(combStrategyInfo.getDealStatus());
            // 开放购买期且当前时间能买
            if (statusEnum == PURCHASE && !canBuy(combStrategyInfo, currentDate)) {
                continue;
            }
            CombineInfoVO combineInfo = null;
            try {
                combineInfo = combInfoService.info(combStrategyInfo.getCombineCode(), integerDate);
                if (combineInfo == null || combineInfo.getCombineName().contains(FILTER_TEXT)) {
                    continue;
                }
            } catch (Exception e) {
                log.warn("[target list] combineCode {} query combine info error", combStrategyInfo.getCombineCode());
                continue;
            }
            CombineStrategyVO combineStrategyVO = new CombineStrategyVO();
            BeanUtil.copyProperties(combineInfo, combineStrategyVO);
            combineStrategyVO.setCombRiskLevel(combineInfo.getCombRiskLevel());
            combineStrategyVO.setTargetProfitRatio(combineInfo.getDisplayValue());
            combineStrategyVO.setDealStatusDesc(statusEnum.getDisplay());
            combineStrategyVO.setOperationPeriod(combineInfo.getOperationPeriod());
            combineStrategyVO.setBuyStartDate(combineInfo.getBuyStartDate());
            if (statusEnum == PURCHASE) {
                combineStrategyVO.setBuyEndTip(assembleBuyEndDate(combStrategyInfo.getBuyEndDate()));
            }
            combineStrategyVOS.add(combineStrategyVO);
        }
        return combineStrategyVOS;
    }

    private boolean canBuy(CombStrategyInfo combStrategyInfo, Date currentDate) {
        Date buyStartDate = assembleDate(combStrategyInfo.getBuyStartDate(), combStrategyInfo.getBuyStartTime());
        Date buyEndDate = assembleDate(combStrategyInfo.getBuyEndDate(), combStrategyInfo.getBuyEndTime());
        return currentDate.after(buyStartDate) && currentDate.before(buyEndDate);
    }

    private Pair<String, String> assembleAgreeContent(List<CombTagDTO> rows) {
        List<String> agreementIds = new ArrayList<>(rows.size());
        List<String> agreementContents = new ArrayList<>(rows.size());
        rows.stream().forEach(row -> {
            if (row.getTag_name().contains(MBYKYC)) {
                agreementIds.add(row.getTag_id());
                agreementContents.add(row.getTag_name());
            }
        });
        return Pair.of(StringUtils.join(agreementIds, JOIN_SPLIT), StringUtils.join(agreementContents, JOIN_SPLIT));
    }

    private String assembleBuyEndDate(Integer buyEndDate) {
        Date endDate = DateUtils.getDate(buyEndDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        Integer month = calendar.get(Calendar.MONTH) + 1;
        Integer day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format(TARGET_BUY_END_TIP, month, day);
    }
}
