package com.foundersc.ifte.invest.adviser.dubbo.service.impl.remote;

import com.foundersc.ifc.portfolio.t2.request.aim.CombStrategyInfoReq;
import com.foundersc.ifc.portfolio.t2.response.aim.CombStrategyInfoDTO;
import com.foundersc.ifc.portfolio.t2.response.aim.CombStrategyInfoResp;
import com.foundersc.ifc.portfolio.t2.response.aim.TargetCombInfoDTO;
import com.foundersc.ifc.portfolio.t2.service.aim.CombStrategyInfoService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombDealStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombStrategyInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombStrategyRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.util.DateUtil;
import com.foundersc.ifte.invest.adviser.dubbo.util.ObjectCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.COMB_STRATEGY_LIST_ERROR;

@Service("combStrategyRemoteService")
@Slf4j
public class CombStrategyRemoteServiceImpl implements CombStrategyRemoteService {

    @Autowired
    private CombStrategyInfoService combStrategyInfoService;

    @Override
    public List<CombStrategyInfo> getCurrCombStrategyList(SimpleAccount simpleAccount, String combineCode, String dealStatus) {
        if (StringUtils.isEmpty(dealStatus) || CombDealStatusEnum.stoppedStatus.contains(dealStatus)) {
            return null;
        }

        List<CombStrategyInfo> combStrategyInfos = getCombStrategyInfos(simpleAccount, combineCode, dealStatus);
        if (CollectionUtils.isEmpty(combStrategyInfos)) {
            return null;
        }
        return sortCurrCombStrategyInfos(combStrategyInfos);
    }

    private List<CombStrategyInfo> sortCurrCombStrategyInfos(List<CombStrategyInfo> combStrategyInfos) {
        return combStrategyInfos.stream().sorted(Comparator.comparing(CombStrategyInfo::getBuyEndDate).thenComparing(CombStrategyInfo::getTargetProfitRatio, Comparator.reverseOrder()).thenComparing(CombStrategyInfo::getCombineCode)).collect(Collectors.toList());
    }

    private List<CombStrategyInfo> sortHisCombStrategyInfos(List<CombStrategyInfo> combStrategyInfos) {
        return combStrategyInfos.stream().sorted(Comparator.comparing(CombStrategyInfo::getBuyStartDate, Comparator.reverseOrder()).thenComparing(CombStrategyInfo::getTargetProfitRatio, Comparator.reverseOrder()).thenComparing(CombStrategyInfo::getCombineCode)).collect(Collectors.toList());
    }

    @Override
    public List<CombStrategyInfo> getCombStrategyList(SimpleAccount simpleAccount, List<String> dealStatuses, Integer beginDate) {
        if (CollectionUtils.isEmpty(dealStatuses)) {
            return null;
        }
        if (beginDate == null) {
            beginDate = DateUtil.dateToInt(DateUtil.lastYearDate());
        }
        List<CombStrategyInfo> combStrategyInfos = new ArrayList<>();
        List<CombStrategyInfo> tempCombStrategy = null;
        for (String dealStatus : dealStatuses) {
            if (!CombDealStatusEnum.allStatus.contains(dealStatus)) {
                continue;
            }
            tempCombStrategy = getCombStrategyInfos(simpleAccount, null, dealStatus);
            if (CollectionUtils.isEmpty(tempCombStrategy)) {
                continue;
            }
            combStrategyInfos.addAll(tempCombStrategy);
        }
        // 根据展示起始日期过滤
        combStrategyInfos = filterCombinesByDate(beginDate, combStrategyInfos);
        if (CollectionUtils.isEmpty(combStrategyInfos)) {
            return combStrategyInfos;
        }
        return sortHisCombStrategyInfos(combStrategyInfos);
    }

    private List<CombStrategyInfo> filterCombinesByDate(Integer beginDate, List<CombStrategyInfo> combStrategyInfos) {
        return combStrategyInfos.stream().filter(combStrategyInfo ->
                combStrategyInfo.getBuyEndDate() >= beginDate
        ).collect(Collectors.toList());
    }

    private List<TargetCombInfoDTO> assembleCombineList(List<CombStrategyInfoDTO> strategyInfoDTOs) {
        List<TargetCombInfoDTO> targetCombInfoDTOs = new ArrayList<>();
        for (CombStrategyInfoDTO strategyInfoDTO : strategyInfoDTOs) {
            if (CollectionUtils.isEmpty(strategyInfoDTO.getTargert_comb_info_ext_dto())) {
                continue;
            }
            targetCombInfoDTOs.addAll(strategyInfoDTO.getTargert_comb_info_ext_dto());
        }

        return targetCombInfoDTOs;
    }

    private List<CombStrategyInfo> getCombStrategyInfos(SimpleAccount simpleAccount, String combineCode, String dealStatus) {
        List<CombStrategyInfoDTO> strategyInfoDTOs = getCombStrategyInfo(simpleAccount, combineCode, dealStatus);
        if (CollectionUtils.isEmpty(strategyInfoDTOs)) {
            return null;
        }
        List<TargetCombInfoDTO> combInfoDTOs = assembleCombineList(strategyInfoDTOs);
        if (CollectionUtils.isEmpty(combInfoDTOs)) {
            return null;
        }
        List<CombStrategyInfo> strategyInfos = new ArrayList<>(combInfoDTOs.size());
        ObjectCopyUtil.copyCombStrategyInfo(combInfoDTOs, strategyInfos);
        return strategyInfos;
    }

    private List<CombStrategyInfoDTO> getCombStrategyInfo(SimpleAccount simpleAccount, String combineCode, String dealStatus) {
        CombStrategyInfoReq combStrategyInfoReq = new CombStrategyInfoReq();
        combStrategyInfoReq.setDealStatus(dealStatus);
        combStrategyInfoReq.setEnCombineCode(combineCode);
        BaseResult<CombStrategyInfoResp> baseResult = combStrategyInfoService.getCombStrategyInfo(simpleAccount, combStrategyInfoReq);
        if (!baseResult.isSuccess()) {
            log.error(COMB_STRATEGY_LIST_ERROR + "getCombStrategyInfo failed, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
        }
        if (baseResult.getData() == null) {
            return null;
        }
        return baseResult.getData().getComb_strategy_info_ext_dto();
    }
}
