package com.foundersc.ifte.invest.adviser.web.service.impl.strategy.purchase;

import com.alibaba.fastjson.JSONObject;
import com.foundersc.ifc.portfolio.t2.enums.v2.FlagEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.bop.EpaperSignStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombRiskLimitTypeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.EligMatchFlag;
import com.foundersc.ifc.portfolio.t2.model.v2.bop.EpaperDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.bop.EpaperSignSerialDTO;
import com.foundersc.ifc.portfolio.t2.request.autotrans.ContractSignReq;
import com.foundersc.ifc.portfolio.t2.response.bank.AutoTransBankResp;
import com.foundersc.ifc.portfolio.t2.response.v2.elig.EligCheckResp;
import com.foundersc.ifc.portfolio.t2.response.v2.trade.AddInvestResp;
import com.foundersc.ifc.portfolio.t2.response.v2.trade.SignAgreementResp;
import com.foundersc.ifc.t2.model.responses.T2ClientInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.TradeTimeCheckRes;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrust;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrustReq;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombEntrustRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.TradeTimeRemoteService;
import com.foundersc.ifte.invest.adviser.web.constants.CommonConstants;
import com.foundersc.ifte.invest.adviser.web.entity.CombRequestRecordEntity;
import com.foundersc.ifte.invest.adviser.web.enums.BoolEnum;
import com.foundersc.ifte.invest.adviser.web.enums.CombRequestEnum;
import com.foundersc.ifte.invest.adviser.web.enums.TradeTypeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.PurchaseReq;
import com.foundersc.ifte.invest.adviser.web.model.trade.PurchaseResp;
import com.foundersc.ifte.invest.adviser.web.model.trade.PurchaseResultVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.TradePreCheckResultVO;
import com.foundersc.ifte.invest.adviser.web.service.CombInfoService;
import com.foundersc.ifte.invest.adviser.web.service.TradeService;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.RecordAsyncWriter;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.T2ServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.TradeCalendarRemoteServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.util.*;
import com.foundersc.itc.product.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.*;
import static com.foundersc.ifte.invest.adviser.web.constants.TipConstants.*;

@Service
@Slf4j
public abstract class PurchaseStrategy {
    @Autowired
    protected CombInfoService combInfoService;
    @Autowired
    protected T2ServiceAdapter t2ServiceAdapter;
    @Autowired
    protected RecordAsyncWriter recordAsyncWriter;
    @Autowired
    private TradeService tradeService;

    @Autowired
    private CombEntrustRemoteService combEntrustRemoteService;

    @Autowired
    private TradeTimeRemoteService timeRemoteService;

    @Autowired
    private TradeCalendarRemoteServiceAdapter tradeCalendarRemoteServiceAdapter;

    private final static int TRADE_CHECK_START_TIME = 93500;

    private final static int TRADE_CHECK_END_TIME = 145500;

    public final PurchaseResultVO tradeProcess(PurchaseReq purchaseReq, CombineInfoVO combineInfo) {
        if (isTargetComb()) {
            checkContinueMode(purchaseReq);
        }
        // 电子合同
        checkElectronicPaper(purchaseReq);
        // 交易信息
        checkTradeInfo(purchaseReq);
        // 适当性
        checkEligibility(purchaseReq);
        // 是否签约
        boolean hasSignedComb = hasSignedComb(purchaseReq.getCombineCode());
        // 金额校验
        checkEntrustAmount(purchaseReq, combineInfo, hasSignedComb);
        // 执行购买操作
        PurchaseResultVO purchaseResult = doPurchase(purchaseReq, combineInfo.getCombineName(), hasSignedComb);

        // 自动转账协议签署
        TradeTimeCheckRes timeCheckRes = timeRemoteService.checkTradeTime(ContextHolder.getSimpleAccount(), ContextHolder.getAppInfo().getVersion());
        if (timeCheckRes.isSupport_7_24()) {
            signAutoTransContract(purchaseReq.getAutoTransFlag(), purchaseResult.getCombRequestNo(), combineInfo);
        }

        return purchaseResult;
    }


    protected abstract void checkContinueMode(PurchaseReq purchaseReq);

    protected abstract PurchaseResultVO doPurchase(PurchaseReq purchaseReq, String combineName, boolean hasSignedComb);

    private void checkElectronicPaper(PurchaseReq purchaseReq) {
        if (!hasSignedEpaper(purchaseReq.getCombineCode())) {
            log.warn("purchase fail, not signed epaper");
            throw new BizException(BizErrorCodeEnum.PURCHASE_ERROR, NOT_SIGNED_EPAPER);
        }
    }

    private void checkTradeInfo(PurchaseReq purchaseReq) {
        TradePreCheckResultVO preCheckResult = tradeService.newPreCheck(purchaseReq.getCombineCode(), TradeTypeEnum.PURCHASE.getTypeCode());
        if (!preCheckResult.getCanTrade()) {
            log.warn("purchase fail, cannot trade for {}", preCheckResult.getCannotTradeTip());
            throw new BizException(BizErrorCodeEnum.PURCHASE_ERROR, preCheckResult.getCannotTradeTip());
        }
    }

    private boolean hasSignedComb(String combineCode) {
        return tradeService.hasSignedComb(combineCode);
    }

    private void checkEntrustAmount(PurchaseReq purchaseReq, CombineInfoVO combineInfo, boolean hasSignedComb) {
        combineInfo.setNeedSign(!hasSignedComb);
        BigDecimal minEntrustAmount = combineInfo.getMinBuyBalance();
        BigDecimal maxEntrustAmount = combineInfo.getMaxBuyBalance();
        if (BigDecimal.ZERO.compareTo(maxEntrustAmount) == 0) {
            log.warn("purchase continue, maxEntrustAmount={}", maxEntrustAmount);
            return;
        }
        if (purchaseReq.getEntrustAmount().compareTo(minEntrustAmount) < 0) {
            log.warn("purchase fail, entrust amount error {}, entrustAmount={}, minEntrustAmount={}",
                    LESS_THAN_MIN_AMOUNT, purchaseReq.getEntrustAmount(), minEntrustAmount);
            throw new BizException(BizErrorCodeEnum.PURCHASE_ERROR, LESS_THAN_MIN_AMOUNT);
        }
        if (purchaseReq.getEntrustAmount().compareTo(maxEntrustAmount) > 0) {
            log.warn("purchase fail, entrust amount error {}, entrustAmount={}, maxEntrustAmount={}",
                    MORE_THAN_MAX_AMOUNT, purchaseReq.getEntrustAmount(), maxEntrustAmount);
            throw new BizException(BizErrorCodeEnum.PURCHASE_ERROR, MORE_THAN_MAX_AMOUNT);
        }
    }


    private void checkEligibility(PurchaseReq purchaseReq) {
        T2ClientInfo t2ClientInfo = t2ServiceAdapter.getT2ClientInfo(ContextHolder.getSimpleAccount());
        if (isIdCardOverdue(t2ClientInfo)) {
            log.warn("checkElig isIdCardOverdue");
            // 身份证过期
            throw new BizException(BizErrorCodeEnum.ID_OVERDUE);
        }
        if (!hasEvalRiskLevel(t2ClientInfo)) {
            log.warn("checkElig not hasEvalRiskLevel");
            // 未进行风险测评
            throw new BizException(BizErrorCodeEnum.NO_RISK_EVAL);
        }
        EligCheckResp eligCheckResp = t2ServiceAdapter.checkElig(ContextHolder.getSimpleAccount(), purchaseReq.getCombineCode());
        if (!eligCheckResp.isSuccess()) {
            log.warn("checkElig fail");
            throw new BizException(BizErrorCodeEnum.ELIG_CHECK_ERROR);
        }
        if (FlagEnum.Y.getCode().equals(eligCheckResp.getExpire_risk_flag())) {
            log.warn("risk flag expire");
            // 风险等级测评过期
            throw new BizException(BizErrorCodeEnum.RISK_EVAL_OVERDUE);
        }
        if (CombRiskLimitTypeEnum.FORBID_TRADE.getCode().equalsIgnoreCase(eligCheckResp.getComb_risk_limit_type())) {
            if (FlagEnum.Y.getCode().equalsIgnoreCase(eligCheckResp.getSenile_flag())) {
                log.warn("senileFlag is true, now allow trade");
                // 超龄不允许下单
                throw new BizException(BizErrorCodeEnum.SENILE_NOT_ALL_TRADE);
            }
            if (EligMatchFlag.NOT_MATCH.getCode().equals(eligCheckResp.getElig_risk_flag())) {
                log.warn("eligRiskFlag is not match");
                // 风险等级不匹配
                throw new BizException(BizErrorCodeEnum.RISK_LEVEL_NOT_MATCH);
            }
            if (EligMatchFlag.NOT_MATCH.getCode().equals(eligCheckResp.getElig_term_flag())) {
                log.warn("eligTermFlag is not match");
                // 投资期限不匹配
                throw new BizException(BizErrorCodeEnum.INVEST_TERM_NOT_MATCH);
            }
            if (EligMatchFlag.NOT_MATCH.getCode().equals(eligCheckResp.getElig_investkind_flag())) {
                log.warn("eligKindFlag is not match");
                // 投资品种不匹配
                throw new BizException(BizErrorCodeEnum.INVEST_KIND_NOT_MATCH);
            }
        }
    }


    /**
     * 是否已经签署电子协议
     * 待签署电子协议【带版本号】都被签署过了，返回true，否则返回false
     *
     * @param combineCode
     * @return
     */
    private boolean hasSignedEpaper(String combineCode) {
        // 查询展示的电子协议列表
        List<EpaperDTO> epaperList = t2ServiceAdapter.queryEpaper(ContextHolder.getSimpleAccount(), combineCode);
        List<EpaperSignSerialDTO> epaperSignSerialDTOS = t2ServiceAdapter.queryEpaperSignSerial(ContextHolder.getSimpleAccount(), combineCode);
        if (CollectionUtils.isEmpty(epaperSignSerialDTOS)) {
            log.info("current date epaperSignSerial is empty, query queryHisEpaperSignSerial");
            epaperSignSerialDTOS = t2ServiceAdapter.queryHisEpaperSignSerial(ContextHolder.getSimpleAccount(), combineCode, DateUtil.lastYearIntDate(), DateUtil.intDate());
        }
        if (CollectionUtils.isEmpty(epaperSignSerialDTOS)) {
            return false;
        }
        // 筛选签署成功的电子协议
        epaperSignSerialDTOS = epaperSignSerialDTOS.stream()
                .filter(x -> EpaperSignStatusEnum.isSignedSuccess(x.getStatus()))
                .collect(Collectors.toList());
        for (EpaperDTO epaperDTO : epaperList) {
            String templateId = epaperDTO.getTemplate_id();
            String versionNo = epaperDTO.getVersion_no();
            boolean hasSigned = false;
            for (EpaperSignSerialDTO epaperSignSerialDTO : epaperSignSerialDTOS) {
                String signedTemplateId = epaperSignSerialDTO.getTemplate_id();
                String signedVersionNo = epaperSignSerialDTO.getVersion_no();
                // 模板编码和版本号都匹配
                if (StringUtils.equals(templateId, signedTemplateId)
                        && StringUtils.equals(versionNo, signedVersionNo)) {
                    log.info("has signed epaper, combineCode={}, templateId={}, versionNo={}", combineCode, templateId, versionNo);
                    hasSigned = true;
                    break;
                }
            }
            if (!hasSigned) {
                log.info("not signed epaper, combineCode={}, templateId={}, versionNo={}", combineCode, templateId, versionNo);
                return false;
            }
        }
        log.info("has signed all epapers, size={}, combineCode={}", epaperList.size(), combineCode);
        return true;
    }

    /**
     * 身份证是否过期
     *
     * @param t2ClientInfo
     * @return
     */
    private boolean isIdCardOverdue(T2ClientInfo t2ClientInfo) {
        Integer idEndDate = t2ClientInfo.getId_enddate();
        log.info("clientId={}, idEndDate={}", t2ClientInfo.getClient_id(), idEndDate);
        if (idEndDate != null && idEndDate.intValue() > 0) {
            int today = Integer.parseInt(DateUtils.formatDate(new Date(), CommonConstants.DEFAULT_DATE_FORMAT));
            // 没有过期返回true,失效当天也不能购买
            if (idEndDate.intValue() > today) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否进行风险等级测评
     *
     * @param t2ClientInfo
     * @return
     */
    private boolean hasEvalRiskLevel(T2ClientInfo t2ClientInfo) {
        return !(t2ClientInfo.getCorp_risk_level() == null || t2ClientInfo.getCorp_risk_level() == 0);
    }

    /**
     * 控制是否调用续期方式接口
     *
     * @return
     */
    public boolean isTargetComb() {
        return false;
    }

    protected CombRequestRecordEntity build(PurchaseReq purchaseReq, String combineName, PurchaseResp purchaseResp, CombRequestEnum combRequestEnum) {
        CombRequestRecordEntity entity = new CombRequestRecordEntity();
        BeanUtil.copyProperties(ContextHolder.getSimpleAccount(), entity);
        entity.setOperCode(combRequestEnum.getCode());
        entity.setOperDesc(combRequestEnum.getDesc());
        entity.setCombCode(purchaseReq.getCombineCode());
        entity.setCombName(combineName);
        entity.setBalance(purchaseReq.getEntrustAmount());
        entity.setResp(JSONObject.toJSONString(purchaseResp));
        entity.setCombRequestNo(purchaseResp.getComb_request_no());
        entity.setOrigCombRequestNo(null);
        entity.setCreateMonth(DateUtil.dateToIntMonth(new Date()));
        entity.setTraceId(MdcUtil.getTraceId());
        entity.setErrorNo(String.valueOf(purchaseResp.getError_no()));
        entity.setErrorInfo(purchaseResp.getError_info());
        return entity;
    }

    /**
     * 组装购买结果页信息
     *
     * @param purchaseReq
     * @param purchaseResp
     * @return
     */
    protected PurchaseResultVO getPurchaseResultVO(PurchaseReq purchaseReq, PurchaseResp purchaseResp) {
        String currTime = formatCurrentTime(purchaseResp);

        TradeTimeCheckRes checkRes = timeRemoteService.checkTradeTime(ContextHolder.getSimpleAccount(), ContextHolder.getAppInfo().getVersion());
        if (!checkRes.isSupport_7_24()) {
            return getTradeTimeResult(purchaseResp, currTime);
        }
        // 确定为非交易日
        if (!tradeCalendarRemoteServiceAdapter.isTradeDate(new Date())) {
            return getNonTradeDateResult(purchaseResp, currTime);
        }
        // 确定为交易时间
        if (checkTradeTime(TRADE_CHECK_START_TIME, TRADE_CHECK_END_TIME)) {
            return getTradeTimeResult(purchaseResp, currTime);
        }
        List<CombineEntrust> entrusts = combEntrustRemoteService.getAllCurrEntrust(ContextHolder.getSimpleAccount(), initEntrustReq(purchaseReq, purchaseResp));
        if (CollectionUtils.isEmpty(entrusts) || entrusts.size() > 1) {
            log.error("[check purchase time] clientId {} requestNo {} result is null.", ContextHolder.getSimpleAccount().getClientId(), purchaseResp.getComb_request_no());
            return new PurchaseResultVO(purchaseResp.getAcpt_id(), purchaseResp.getComb_request_no(), PURCHASE_RESULT_TITLE, PURCHASE_RESULT_TIP);
        }
        CombineEntrust currentEntrust = entrusts.get(0);
        // 非交易时间下单
        if (currentEntrust.reserveEntrust()) {
            return getNonTradeTimeResult(purchaseResp, currTime);
        } else {
            return getTradeTimeResult(purchaseResp, currTime);
        }
    }

    private String formatCurrentTime(PurchaseResp purchaseResp) {
        if (purchaseResp.getCurr_date() == null || purchaseResp.getCurr_time() == null) {
            Date date = new Date();
            purchaseResp.setCurr_date(DateUtils.getCurrentIntegerDate());
            purchaseResp.setCurr_time(Integer.valueOf(DateUtils.formatDate(date, LOCAL_TIME_FORMAT)));
            return DateUtils.formatDate(date, DEFAULT_DATE_TIME_FORMAT);
        }
        String time = purchaseResp.getCurr_date().toString().concat(TargetDateUtil.formatTime(purchaseResp.getCurr_time().toString()));
        Date date = DateUtils.parseDate(time, DateUtils.YYYYMMDDHHMMSS);
        return DateUtils.formatDate(date, DEFAULT_DATE_TIME_FORMAT);
    }

    private boolean checkTradeTime(int startIntTime, int endIntTime) {
        LocalTime beginTime = DateUtil.intToLocalTime(startIntTime);
        LocalTime endTime = DateUtil.intToLocalTime(endIntTime);
        return isInTradeTime(beginTime, endTime);
    }

    private boolean isInTradeTime(LocalTime beginTime, LocalTime endTime) {
        if (!tradeCalendarRemoteServiceAdapter.isTradeDate(new Date())) {
            return false;
        }
        LocalTime now = LocalTime.now();
        return (now.isAfter(beginTime) || now.equals(beginTime)) && now.isBefore(endTime);
    }

    /**
     * 不确定为交易时间下单结果 需要通过组合申请状态判断是否为交易时间下单
     *
     * @param purchaseResp
     * @param currTime
     * @return
     */
    private PurchaseResultVO getNonTradeTimeResult(PurchaseResp purchaseResp, String currTime) {
        Calendar calendar = Calendar.getInstance();
        if (purchaseResp.getCurr_time() <= CHECK_TIME) {
            PurchaseResultVO resultVO = new PurchaseResultVO(purchaseResp.getAcpt_id(), purchaseResp.getComb_request_no(), PRE_PURCHASE_RESULT_TITLE, PURCHASE_RESULT_TIP);
            Integer acceptDate = tradeCalendarRemoteServiceAdapter.queryNextTradeDate(calendar.getTime());
            resultVO.setFlowList(PurchaseResultVO.getNonTradeTimeFlow(currTime, acceptDate.toString(), purchaseResp.getPre_affirm_date().toString(), purchaseResp.getPre_income_date().toString()));
            return resultVO;
        }
        if (purchaseResp.getCurr_time() > CHECK_TIME) {
            PurchaseResultVO resultVO = new PurchaseResultVO(purchaseResp.getAcpt_id(), purchaseResp.getComb_request_no(), PRE_PURCHASE_RESULT_TITLE, PURCHASE_RESULT_TIP);
            calendar.add(Calendar.DATE, 1);
            Integer acceptDate = tradeCalendarRemoteServiceAdapter.queryNextTradeDate(calendar.getTime());
            resultVO.setFlowList(PurchaseResultVO.getNonTradeTimeFlow(currTime, acceptDate.toString(), purchaseResp.getPre_affirm_date().toString(), purchaseResp.getPre_income_date().toString()));
            return resultVO;
        }
        return null;
    }

    private void signAutoTransContract(Integer autoTransFlag, String combRequestNo, CombineInfoVO combineInfo) {
        if (autoTransFlag == null || BoolEnum.NO.getId().equals(autoTransFlag)) {
            return;
        }
        AutoTransBankResp autoTransBankResp = t2ServiceAdapter.getAutoTransBank(ContextHolder.getSimpleAccount());
        ContractSignReq contractSignReq = new ContractSignReq();
        contractSignReq.setCombineCode(combineInfo.getCombineCode());
        contractSignReq.setCombineName(combineInfo.getCombineName());
        contractSignReq.setRelatedRequestNo(combRequestNo);
        if (autoTransBankResp != null) {
            contractSignReq.setBankNo(autoTransBankResp.getBank_no());
        }
        t2ServiceAdapter.signAutoTransContract(ContextHolder.getSimpleAccount(), contractSignReq);
    }

    /**
     * 非交易日结果处理
     *
     * @param purchaseResp
     * @param currTime
     * @return
     */
    private PurchaseResultVO getNonTradeDateResult(PurchaseResp purchaseResp, String currTime) {
        PurchaseResultVO resultVO = new PurchaseResultVO(purchaseResp.getAcpt_id(), purchaseResp.getComb_request_no(), PRE_PURCHASE_RESULT_TITLE, PURCHASE_RESULT_TIP);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Integer acceptDate = tradeCalendarRemoteServiceAdapter.queryNextTradeDate(calendar.getTime());
        resultVO.setFlowList(PurchaseResultVO.getNonTradeTimeFlow(currTime, acceptDate.toString(), purchaseResp.getPre_affirm_date().toString(), purchaseResp.getPre_income_date().toString()));
        return resultVO;
    }

    /**
     * 交易时间下单结果 9:40-14:50
     *
     * @param purchaseResp
     * @param currTime
     * @return
     */
    private PurchaseResultVO getTradeTimeResult(PurchaseResp purchaseResp, String currTime) {
        PurchaseResultVO resultVO = new PurchaseResultVO(purchaseResp.getAcpt_id(), purchaseResp.getComb_request_no(), PURCHASE_RESULT_TITLE, PURCHASE_RESULT_TIP);
        resultVO.setFlowList(PurchaseResultVO.getTradeTimeFlow(currTime, purchaseResp.getPre_affirm_date().toString(), purchaseResp.getPre_income_date().toString()));
        return resultVO;
    }

    private CombineEntrustReq initEntrustReq(PurchaseReq purchaseReq, PurchaseResp purchaseResp) {
        CombineEntrustReq entrustReq = new CombineEntrustReq();
        entrustReq.setCombineCode(purchaseReq.getCombineCode());
        entrustReq.setCombRequestNo(purchaseResp.getComb_request_no());
        entrustReq.setBeginDate(purchaseResp.getInit_date());
        entrustReq.setEndDate(purchaseResp.getInit_date());
        return entrustReq;
    }

    protected PurchaseResp getPurchaseResp(SignAgreementResp signAgreementResp) {
        PurchaseResp resp = new PurchaseResp();
        BeanUtil.copyProperties(signAgreementResp, resp);
        return resp;
    }

    protected PurchaseResp getPurchaseResp(AddInvestResp addInvestResp) {
        PurchaseResp resp = new PurchaseResp();
        BeanUtil.copyProperties(addInvestResp, resp);
        return resp;
    }
}
