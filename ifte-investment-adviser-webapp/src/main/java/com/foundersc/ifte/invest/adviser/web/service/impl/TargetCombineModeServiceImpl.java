package com.foundersc.ifte.invest.adviser.web.service.impl;

import com.foundersc.ifc.portfolio.t2.enums.v2.trade.AgreementStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.trade.OperationalDealModeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.trade.StopProfitModeEnum;
import com.foundersc.ifc.portfolio.t2.model.v2.trade.AgreementExtDTO;
import com.foundersc.ifc.portfolio.t2.response.aim.ContractChangeResp;
import com.foundersc.ifc.portfolio.t2.response.v2.trade.QueryAgreementResp;
import com.foundersc.ifte.invest.adviser.web.constants.TipConstants;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.ClientTargetCombModeVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.FlagAndTipVO;
import com.foundersc.ifte.invest.adviser.web.service.CombInfoService;
import com.foundersc.ifte.invest.adviser.web.service.TargetCombineModeService;
import com.foundersc.ifte.invest.adviser.web.service.TradeService;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.T2ServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author wangfuwei
 * @date 2023/8/29
 */
@Service
@Slf4j
public class TargetCombineModeServiceImpl implements TargetCombineModeService {
    @Autowired
    private T2ServiceAdapter t2ServiceAdapter;

    @Autowired
    private CombInfoService combInfoService;

    @Autowired
    private TradeService tradeService;

    @Override
    public ClientTargetCombModeVO queryModeInfo(String combineCode) {
        CombineInfoVO combInfo = combInfoService.info(combineCode, DateUtil.intDate());
        // 查询是否有【已生效】签约
        String signedMode = getSignedMode(combineCode);
        if (StringUtils.isNotBlank(signedMode)) {
            return new ClientTargetCombModeVO(signedMode, TipConstants.getProfitModeTip(combInfo.getInvestOrganNo()));
        }
        // 默认返回：自动转出，即0
        return new ClientTargetCombModeVO(StopProfitModeEnum.REDEEM.getCode(), TipConstants.getProfitModeTip(combInfo.getInvestOrganNo()));
    }

    @Override
    public ClientTargetCombModeVO queryModeInfoByInvestOrganNo(String combineCode, String investOrganNo) {
        // 查询是否有【已生效】签约
        String signedMode = getSignedMode(combineCode);
        if (StringUtils.isNotBlank(signedMode)) {
            return new ClientTargetCombModeVO(signedMode, TipConstants.getProfitModeTip(investOrganNo));
        }
        // 默认返回：自动转出，即0
        return new ClientTargetCombModeVO(StopProfitModeEnum.REDEEM.getCode(), TipConstants.getProfitModeTip(investOrganNo));
    }

    /**
     * 获取签约的续期方式
     *
     * @param combineCode
     * @return 如果没签约则返回null
     */
    private String getSignedMode(String combineCode) {
        // 查询是否有【已生效】签约
        QueryAgreementResp queryAgreementResp = t2ServiceAdapter.queryAgreement(ContextHolder.getSimpleAccount(), combineCode);
        Optional<AgreementExtDTO> optional = queryAgreementResp.getRows().stream()
                .filter(x -> AgreementStatusEnum.VALID.getCode().equals(x.getAgreement_status()))
                .findAny();
        if (optional.isPresent()) {
            return optional.get().getStop_profit_mode();
        }
        return null;
    }

    @Override
    public FlagAndTipVO modifyMode(String combineCode, String newMode) {
        String modifyTip = canModifyMode(combineCode);
        if (StringUtils.isNotBlank(modifyTip)) {
            return new FlagAndTipVO(false, modifyTip);
        }
        String signedMode = getSignedMode(combineCode);
        if (StringUtils.equals(signedMode, newMode)) {
            log.warn("mode not change, signedMode={}, newMode={}", signedMode, newMode);
            return new FlagAndTipVO();
        }
        StopProfitModeEnum stopProfitMode = StopProfitModeEnum.parseByCode(newMode);
        OperationalDealModeEnum operationalDealMode = OperationalDealModeEnum.parseByCode(newMode);
        if (stopProfitMode == null || operationalDealMode == null) {
            throw new IllegalArgumentException("续期方式错误");
        }
        ContractChangeResp contractChangeResp = t2ServiceAdapter.changeProfitContract(ContextHolder.getSimpleAccount(), combineCode, stopProfitMode, operationalDealMode);
        if (!contractChangeResp.isSuccess()) {
            return new FlagAndTipVO(false, contractChangeResp.getError_info());
        }
        return new FlagAndTipVO();
    }

    /**
     * 是否可以修改续期方式
     * 已止盈或到期日当天，不能修改续期方式
     *
     * @param combineCode
     * @return
     */
    private String canModifyMode(String combineCode) {
        CombineInfoVO combInfo = combInfoService.info(combineCode, DateUtil.intDate());
        if (DateUtil.isValidDate(combInfo.getProfitValidDate()) && DateUtil.strToIntDate(combInfo.getProfitValidDate()) <= DateUtil.intDate()) {
            // 已止盈
            return "策略已止盈，不支持修改";
        }
        if (DateUtil.isValidDate(combInfo.getCombEndDate()) && DateUtil.strToIntDate(combInfo.getCombEndDate()) == DateUtil.intDate()) {
            // 到期日当天不允许修改续期方式
            return "策略今日到期，不支持修改";
        }
        if (DateUtil.isValidDate(combInfo.getCombEndDate()) && DateUtil.strToIntDate(combInfo.getCombEndDate()) < DateUtil.intDate()) {
            // 已到期不允许修改续期方式
            return "策略已到期，不支持修改";
        }
        // 解约中不支持修改
        if (tradeService.isCancellingComb(combineCode)) {
            return "目标盈解约中，不支持修改";
        }
        return null;
    }

}
