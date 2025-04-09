package com.foundersc.ifte.invest.adviser.web.service.impl.support;

import com.foundersc.ifc.portfolio.t2.enums.v2.FlagEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombBusinTypeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CopywritingTypeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.EligConfirmTypeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.trade.ExchTimeKindEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.trade.OperationalDealModeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.trade.StopProfitModeEnum;
import com.foundersc.ifc.portfolio.t2.model.v2.bop.EpaperDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.bop.EpaperSignSerialDTO;
import com.foundersc.ifc.portfolio.t2.request.aim.ContractChangeReq;
import com.foundersc.ifc.portfolio.t2.request.aim.PreAddInvestReq;
import com.foundersc.ifc.portfolio.t2.request.autotrans.ContractSignReq;
import com.foundersc.ifc.portfolio.t2.request.v2.bop.DownloadEpaperTemplateReq;
import com.foundersc.ifc.portfolio.t2.request.v2.bop.QueryEpaperReq;
import com.foundersc.ifc.portfolio.t2.request.v2.bop.QueryEpaperSignSerialReq;
import com.foundersc.ifc.portfolio.t2.request.v2.bop.SignEpaperReq;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.QueryCombFareArgReq;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.QueryCombTagReq;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.QueryCopywritingExtReq;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.QueryTradeDateRuleReq;
import com.foundersc.ifc.portfolio.t2.request.v2.elig.EligCheckReq;
import com.foundersc.ifc.portfolio.t2.request.v2.kyc.TargetKycAnswerReq;
import com.foundersc.ifc.portfolio.t2.request.v2.trade.*;
import com.foundersc.ifc.portfolio.t2.response.aim.ContractChangeResp;
import com.foundersc.ifc.portfolio.t2.response.autotrans.ContractSignResponse;
import com.foundersc.ifc.portfolio.t2.response.bank.AutoTransBankResp;
import com.foundersc.ifc.portfolio.t2.response.bank.BankInfoResp;
import com.foundersc.ifc.portfolio.t2.response.v2.bop.DownloadEpaperTemplateResp;
import com.foundersc.ifc.portfolio.t2.response.v2.bop.SignEpaperResp;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCombFareArgResp;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCombTagResp;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryCopywritingExtResp;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryTradeDateRuleResp;
import com.foundersc.ifc.portfolio.t2.response.v2.elig.EligCheckResp;
import com.foundersc.ifc.portfolio.t2.response.v2.elig.QueryClientPreferExtResp;
import com.foundersc.ifc.portfolio.t2.response.v2.kyc.TargetAnswerResp;
import com.foundersc.ifc.portfolio.t2.response.v2.trade.*;
import com.foundersc.ifc.portfolio.t2.service.aim.CombStrategyTradeService;
import com.foundersc.ifc.portfolio.t2.service.autotrans.CombAutoTransService;
import com.foundersc.ifc.portfolio.t2.service.bank.BankService;
import com.foundersc.ifc.portfolio.t2.service.v2.*;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifc.t2.model.responses.T2ClientInfo;
import com.foundersc.ifc.t2.service.AccountInfoService;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.util.BeanUtil;
import com.foundersc.ifte.invest.adviser.web.util.DataUtil;
import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import com.foundersc.ifte.invest.adviser.web.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.BLANK_STR;
import static com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum.*;

/**
 * @author wangfuwei
 * @date 2022/9/29
 */
@Slf4j
@Component
public class T2ServiceAdapter {
    private final static String TG_PROD_TA_NO = "!";
    private final static String TG_FINANCE_TYPE = "9";
    private final static String MONEY_TYPE = "0";

    @Autowired
    private CombineService combineService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private EligibilityService eligibilityService;

    @Autowired
    private BopService bopService;

    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private TradeCalendarRemoteServiceAdapter tradeCalendarRemoteServiceAdapter;

    @Autowired
    private CombStrategyTradeService combStrategyTradeService;

    @Autowired
    private BankService bankService;

    @Autowired
    private KycPaperService kycPaperService;

    @Autowired
    private CombAutoTransService combAutoTransService;

    /**
     * 是否允许交易检查
     *
     * @param simpleAccount
     * @return
     */
    public ExchtimeTransCheckExtResp exchtimeTransCheck(SimpleAccount simpleAccount) {
        BaseResult<ExchtimeTransCheckExtResp> baseResult = tradeService.exchtimeTransCheck(simpleAccount);
        if (!baseResult.isSuccess()) {
            log.error("exchtimeTransCheck t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.EXCHTIME_TRANS_CHECK_ERROR, baseResult.getErrorMsg());
        }
        if (baseResult.getData() == null) {
            log.error("exchtimeTransCheck data is null");
            throw new BizException(BizErrorCodeEnum.EXCHTIME_TRANS_CHECK_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("exchtimeTransCheck error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.EXCHTIME_TRANS_CHECK_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 组合签约查询
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    public QueryAgreementResp queryAgreement(SimpleAccount simpleAccount, String combineCode) {
        QueryAgreementReq queryAgreementReq = new QueryAgreementReq();
        queryAgreementReq.setCombineCode(combineCode);
        BaseResult<QueryAgreementResp> baseResult = tradeService.queryAgreement(simpleAccount, queryAgreementReq);
        if (!baseResult.isSuccess()) {
            log.error("queryAgreement t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_COMB_AGREEMENT_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("queryAgreement data is null");
            throw new BizException(BizErrorCodeEnum.QUERY_COMB_AGREEMENT_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("queryAgreement error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.QUERY_COMB_AGREEMENT_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 查询投顾交易时间
     *
     * @param simpleAccount
     * @param exchTimeKind
     * @return
     */
    public QueryExchTimeResp queryExchTime(SimpleAccount simpleAccount, ExchTimeKindEnum exchTimeKind) {
        QueryExchTimeReq queryExchTimeReq = new QueryExchTimeReq();
        queryExchTimeReq.setExchTimeKind(exchTimeKind);
        BaseResult<QueryExchTimeResp> baseResult = tradeService.queryExchTime(simpleAccount, queryExchTimeReq);
        if (!baseResult.isSuccess()) {
            log.error("queryExchTime t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_EXCH_TIME_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("queryExchTime data is null");
            throw new BizException(BizErrorCodeEnum.QUERY_EXCH_TIME_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("queryExchTime error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.QUERY_EXCH_TIME_ERROR);
        }
        if (CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.error("queryExchTime exchtimeExts is empty");
            throw new BizException(BizErrorCodeEnum.QUERY_EXCH_TIME_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 组合适当性校验
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    public EligCheckResp checkElig(SimpleAccount simpleAccount, String combineCode) {
        EligCheckReq eligCheckReq = new EligCheckReq();
        eligCheckReq.setCombineCode(combineCode);
        BaseResult<EligCheckResp> baseResult = eligibilityService.checkElig(simpleAccount, eligCheckReq);
        if (!baseResult.isSuccess()) {
            log.error("checkElig t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.ELIG_CHECK_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("checkElig data is null");
            throw new BizException(BizErrorCodeEnum.ELIG_CHECK_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 客户风险偏好查询
     *
     * @param simpleAccount
     * @return
     */
    public QueryClientPreferExtResp queryClientPreferExt(SimpleAccount simpleAccount) {
        BaseResult<QueryClientPreferExtResp> baseResult = eligibilityService.queryClientPreferExt(simpleAccount);
        if (!baseResult.isSuccess()) {
            log.error("queryClientPreferExt t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_CLIENT_PREFER_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("queryClientPreferExt data is null");
            throw new BizException(BizErrorCodeEnum.QUERY_CLIENT_PREFER_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("queryClientPreferExt error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.QUERY_CLIENT_PREFER_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 查询账户余额
     *
     * @param simpleAccount
     * @return
     */
    public QueryEnableBalanceResp queryEnableBalance(SimpleAccount simpleAccount) {
        BaseResult<QueryEnableBalanceResp> baseResult = tradeService.queryEnableBalance(simpleAccount);
        if (!baseResult.isSuccess()) {
            log.error("queryEnableBalance t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_ENABLE_BALANCE_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("queryEnableBalance data is null");
            throw new BizException(BizErrorCodeEnum.QUERY_ENABLE_BALANCE_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("queryEnableBalance error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.QUERY_ENABLE_BALANCE_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 组合交易规则日期获取
     *
     * @param simpleAccount
     * @param combineCode
     * @param initDate
     * @return
     */
    public QueryTradeDateRuleResp queryTradeDateRule(SimpleAccount simpleAccount, String combineCode, CombBusinTypeEnum combBusinType, int initDate) {
        QueryTradeDateRuleReq queryTradeDateRuleReq = new QueryTradeDateRuleReq();
        queryTradeDateRuleReq.setCombineCode(combineCode);
        queryTradeDateRuleReq.setInitDate(initDate);
        queryTradeDateRuleReq.setCombBusinType(combBusinType);
        BaseResult<QueryTradeDateRuleResp> baseResult = combineService.queryTradeDateRule(simpleAccount, queryTradeDateRuleReq);
        if (!baseResult.isSuccess()) {
            log.error("queryTradeDateRule t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_TRADE_DATE_RULE_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("queryTradeDateRule data is null");
            throw new BizException(BizErrorCodeEnum.QUERY_TRADE_DATE_RULE_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("queryTradeDateRule error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.QUERY_TRADE_DATE_RULE_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 组合服务费参数查询
     *
     * @param simpleAccount
     * @param combChargeNo
     * @return
     */
    public QueryCombFareArgResp queryCombFareArg(SimpleAccount simpleAccount, Integer combChargeNo) {
        QueryCombFareArgReq queryCombFareArgReq = new QueryCombFareArgReq();
        queryCombFareArgReq.setCombChargeNo(String.valueOf(combChargeNo));
        BaseResult<QueryCombFareArgResp> baseResult = combineService.queryCombFareArg(simpleAccount, queryCombFareArgReq);
        if (!baseResult.isSuccess()) {
            log.error("queryCombFareArg t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_COMB_FARE_ARG_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("queryCombFareArg data is null");
            throw new BizException(BizErrorCodeEnum.QUERY_COMB_FARE_ARG_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("queryCombFareArg error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.QUERY_COMB_FARE_ARG_ERROR);
        }
        if (CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.error("queryCombFareArg combFareArgs is empty");
            throw new BizException(BizErrorCodeEnum.QUERY_COMB_FARE_ARG_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 查询投顾电子协议
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    public List<EpaperDTO> queryEpaper(SimpleAccount simpleAccount, String combineCode) {
        QueryEpaperReq queryEpaperReq = new QueryEpaperReq();
        queryEpaperReq.setProdtaNo(TG_PROD_TA_NO);
        queryEpaperReq.setProdCode(combineCode);
        queryEpaperReq.setFinanceType(TG_FINANCE_TYPE);
        queryEpaperReq.setProdType(BLANK_STR);
        BaseResult<List<EpaperDTO>> baseResult = bopService.queryEpaper(simpleAccount, queryEpaperReq);
        if (!baseResult.isSuccess()) {
            log.error("queryEpaper t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_EPAPER_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("queryEpaper data is null");
            throw new BizException(BizErrorCodeEnum.QUERY_EPAPER_ERROR);
        }
        if (CollectionUtils.isEmpty(baseResult.getData())) {
            log.error("queryEpaper list is empty");
            throw new BizException(BizErrorCodeEnum.QUERY_EPAPER_ERROR);
        }
        List<EpaperDTO> epaperDTOList = baseResult.getData();
        // 根据orderNo从小到大排
        return epaperDTOList.stream().sorted(Comparator.comparing(EpaperDTO::getOrder_no)).collect(Collectors.toList());
    }

    /**
     * 电子协议模板下载
     *
     * @param simpleAccount
     * @param templateId
     * @param versionNo
     * @return
     */
    public DownloadEpaperTemplateResp downloadEpaperTemplate(SimpleAccount simpleAccount, String templateId, String versionNo) {
        DownloadEpaperTemplateReq downloadEpaperTemplateReq = new DownloadEpaperTemplateReq();
        downloadEpaperTemplateReq.setTemplateId(templateId);
        downloadEpaperTemplateReq.setVersionNo(versionNo);
        BaseResult<DownloadEpaperTemplateResp> baseResult = bopService.downloadEpaperTemplate(simpleAccount, downloadEpaperTemplateReq);
        if (!baseResult.isSuccess()) {
            log.error("downloadEpaperTemplate t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.DOWNLOAD_EPAPER_TEMPLATE_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("downloadEpaperTemplate data is null");
            throw new BizException(BizErrorCodeEnum.DOWNLOAD_EPAPER_TEMPLATE_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("downloadEpaperTemplate error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.DOWNLOAD_EPAPER_TEMPLATE_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 电子协议签署
     *
     * @param simpleAccount
     * @param templateId
     * @param extSerialNo
     * @param versionNo
     * @param synchroFlag   接口同步标志：0-异步，1-同步（默认）
     * @return
     */
    public SignEpaperResp signEpaper(SimpleAccount simpleAccount, String combineCode, String templateId, String extSerialNo,
                                     String versionNo, FlagEnum synchroFlag) {
        SignEpaperReq signEpaperReq = new SignEpaperReq();
        // 此处需要传组合代码，否则电子协议签署流水查询会查不到
        signEpaperReq.setProdCode(combineCode);
        signEpaperReq.setProdtaNo(TG_PROD_TA_NO);
        signEpaperReq.setFinanceType(TG_FINANCE_TYPE);
        signEpaperReq.setProdType(BLANK_STR);
        signEpaperReq.setTemplateId(templateId);
        signEpaperReq.setExtSerialNo(extSerialNo);
        signEpaperReq.setVersionNo(versionNo);
        signEpaperReq.setSynchroFlag(synchroFlag.getCode());
        BaseResult<SignEpaperResp> baseResult = bopService.signEpaper(simpleAccount, signEpaperReq);
        if (!baseResult.isSuccess()) {
            log.error("signEpaper t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.SIGN_EPAPER_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("signEpaper data is null");
            throw new BizException(BizErrorCodeEnum.SIGN_EPAPER_ERROR);
        }
        if (baseResult.getData().getError_no() != 0) {
            log.error("signEpaper error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.SIGN_EPAPER_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 电子协议签署流水查询（当天）
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    public List<EpaperSignSerialDTO> queryEpaperSignSerial(SimpleAccount simpleAccount, String combineCode) {
        QueryEpaperSignSerialReq queryEpaperSignSerialReq = new QueryEpaperSignSerialReq();
        queryEpaperSignSerialReq.setCombineCode(combineCode);
        queryEpaperSignSerialReq.setProdtaNo(TG_PROD_TA_NO);
        queryEpaperSignSerialReq.setFinanceType(TG_FINANCE_TYPE);
        queryEpaperSignSerialReq.setProdType(BLANK_STR);
        BaseResult<List<EpaperSignSerialDTO>> baseResult = bopService.queryEpaperSignSerial(simpleAccount, queryEpaperSignSerialReq);
        if (!baseResult.isSuccess()) {
            log.error("queryEpaperSignSerial t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_EPAPER_SIGN_SERIAL_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 电子协议签署流水查询（归档）
     *
     * @param simpleAccount
     * @param combineCode
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<EpaperSignSerialDTO> queryHisEpaperSignSerial(SimpleAccount simpleAccount, String combineCode, int beginDate, int endDate) {
        QueryEpaperSignSerialReq queryEpaperSignSerialReq = new QueryEpaperSignSerialReq();
        queryEpaperSignSerialReq.setCombineCode(combineCode);
        queryEpaperSignSerialReq.setProdtaNo(TG_PROD_TA_NO);
        queryEpaperSignSerialReq.setFinanceType(TG_FINANCE_TYPE);
        queryEpaperSignSerialReq.setProdType(BLANK_STR);
        queryEpaperSignSerialReq.setBeginDate(beginDate);
        queryEpaperSignSerialReq.setEndDate(endDate);
        queryEpaperSignSerialReq.setIsArchive("1");
        queryEpaperSignSerialReq.setQueryFlag("1");
        BaseResult<List<EpaperSignSerialDTO>> baseResult = bopService.queryEpaperSignSerial(simpleAccount, queryEpaperSignSerialReq);
        if (!baseResult.isSuccess()) {
            log.error("queryHisEpaperSignSerial t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_HIS_EPAPER_SIGN_SERIAL_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 组合签约
     *
     * @param simpleAccount
     * @param combineCode
     * @param signBalance
     * @return
     */
    public SignAgreementResp signAgreement(SimpleAccount simpleAccount, String combineCode, BigDecimal signBalance, String continueMode) {
        SignAgreementReq signAgreementReq = new SignAgreementReq();
        signAgreementReq.setCombineCode(combineCode);
        signAgreementReq.setSignBalance(signBalance);
        if (!StringUtils.isEmpty(continueMode)) {
            signAgreementReq.setOperationalDealMode(OperationalDealModeEnum.parseByCode(continueMode));
            signAgreementReq.setStopProfitMode(StopProfitModeEnum.parseByCode(continueMode));
        }
        // 使用当日交易日，pre环境全网测试使用当天
        if (EnvUtil.isPre()) {
            signAgreementReq.setBeginDate(DateUtil.intDate());
        } else {
            signAgreementReq.setBeginDate(tradeCalendarRemoteServiceAdapter.queryNextTradeDate(new Date()));
        }
        signAgreementReq.setEligConfirmType(EligConfirmTypeEnum.MATCH_AND_CONFIRM);
        BaseResult<SignAgreementResp> baseResult = tradeService.signAgreement(simpleAccount, signAgreementReq);
        if (!baseResult.isSuccess()) {
            log.error("signAgreement t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            SignAgreementResp signAgreementResp = new SignAgreementResp();
            signAgreementResp.setError_no(DataUtil.strToInt(baseResult.getCode()));
            signAgreementResp.setError_info(baseResult.getErrorMsg());
            return signAgreementResp;
        }
        if (baseResult.getData() == null) {
            log.error("signAgreement data is null");
            throw new BizException(BizErrorCodeEnum.SIGN_AGREEMENT_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("signAgreement error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
        }
        return baseResult.getData();
    }

    /**
     * 追加投资
     *
     * @param simpleAccount
     * @param combineCode
     * @param entrustBalance
     * @return
     */
    public AddInvestResp addInvest(SimpleAccount simpleAccount, String combineCode, BigDecimal entrustBalance) {
        AddInvestReq addInvestReq = new AddInvestReq();
        addInvestReq.setCombineCode(combineCode);
        addInvestReq.setEntrustBalance(entrustBalance);
        BaseResult<AddInvestResp> baseResult = tradeService.addInvest(simpleAccount, addInvestReq);
        if (!baseResult.isSuccess()) {
            log.error("addInvest t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            AddInvestResp addInvestResp = new AddInvestResp();
            addInvestResp.setError_no(DataUtil.strToInt(baseResult.getCode()));
            addInvestResp.setError_info(baseResult.getErrorMsg());
            return addInvestResp;
        }
        if (baseResult.getData() == null) {
            log.error("addInvest data is null");
            throw new BizException(BizErrorCodeEnum.ADD_INVEST_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("addInvest error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
        }
        return baseResult.getData();
    }

    /**
     * 目标盈预约追加
     *
     * @param simpleAccount
     * @param combineCode
     * @param entrustBalance
     * @return
     */
    public AddInvestResp reserveAddInvest(SimpleAccount simpleAccount, String combineCode, BigDecimal entrustBalance) {
        PreAddInvestReq addInvestReq = new PreAddInvestReq();
        addInvestReq.setCombineCode(combineCode);
        addInvestReq.setEntrustBalance(entrustBalance);
        BaseResult<AddInvestResp> baseResult = combStrategyTradeService.preAddInvest(simpleAccount, addInvestReq);
        if (!baseResult.isSuccess()) {
            log.error("reserveAddInvest t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            AddInvestResp addInvestResp = new AddInvestResp();
            addInvestResp.setError_no(DataUtil.strToInt(baseResult.getCode()));
            addInvestResp.setError_info(baseResult.getErrorMsg());
            return addInvestResp;
        }
        if (baseResult.getData() == null) {
            log.error("reserveAddInvest data is null");
            throw new BizException(BizErrorCodeEnum.PRE_ADD_INVEST_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("reserveAddInvest error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
        }
        return baseResult.getData();
    }


    /**
     * 组合申请撤单
     *
     * @param simpleAccount
     * @param origCombRequestNo
     * @return
     */
    public RecallCombRequestResp recallCombRequest(SimpleAccount simpleAccount, String origCombRequestNo) {
        RecallCombRequestReq recallCombRequestReq = new RecallCombRequestReq();
        recallCombRequestReq.setOrigCombRequestNo(origCombRequestNo);
        BaseResult<RecallCombRequestResp> baseResult = tradeService.recallCombRequest(simpleAccount, recallCombRequestReq);
        if (!baseResult.isSuccess()) {
            log.error("recallCombRequest t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            RecallCombRequestResp recallCombRequestResp = new RecallCombRequestResp();
            recallCombRequestResp.setError_no(DataUtil.strToInt(baseResult.getCode()));
            recallCombRequestResp.setError_info(baseResult.getErrorMsg());
            return recallCombRequestResp;
        }
        if (baseResult.getData() == null) {
            log.error("recallCombRequest data is null");
            throw new BizException(BizErrorCodeEnum.RECALL_COMB_REQUEST_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("recallCombRequest error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
        }
        return baseResult.getData();
    }

    /**
     * 目标盈组合申请撤单
     *
     * @param simpleAccount
     * @param origCombRequestNo
     * @return
     */
    public RecallCombRequestResp recallTargetCombRequest(SimpleAccount simpleAccount, String origCombRequestNo) {
        RecallCombRequestReq recallCombRequestReq = new RecallCombRequestReq();
        recallCombRequestReq.setOrigCombRequestNo(origCombRequestNo);
        BaseResult<RecallCombRequestResp> baseResult = combStrategyTradeService.preRecallInvest(simpleAccount, recallCombRequestReq);
        if (!baseResult.isSuccess()) {
            log.error("recallTargetCombRequest t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            RecallCombRequestResp recallCombRequestResp = new RecallCombRequestResp();
            recallCombRequestResp.setError_no(DataUtil.strToInt(baseResult.getCode()));
            recallCombRequestResp.setError_info(baseResult.getErrorMsg());
            return recallCombRequestResp;
        }
        if (baseResult.getData() == null) {
            log.error("recallTargetCombRequest data is null");
            throw new BizException(BizErrorCodeEnum.RECALL_COMB_REQUEST_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("recallTargetCombRequest error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
        }
        return baseResult.getData();
    }

    /**
     * 客户组合可取资产查询
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    public QueryAssetFetchExtResp queryAssetFetchExt(SimpleAccount simpleAccount, String combineCode) {
        QueryAssetFetchExtReq queryAssetFetchExtReq = new QueryAssetFetchExtReq();
        queryAssetFetchExtReq.setCombineCode(combineCode);
        BaseResult<QueryAssetFetchExtResp> baseResult = tradeService.queryAssetFetchExt(simpleAccount, queryAssetFetchExtReq);
        if (!baseResult.isSuccess()) {
            log.error("queryAssetFetchExt t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.QUERY_ASSET_FETCH_EXT_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error("queryAssetFetchExt data is null");
            throw new BizException(BizErrorCodeEnum.QUERY_ASSET_FETCH_EXT_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("queryAssetFetchExt error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.QUERY_ASSET_FETCH_EXT_ERROR);
        }
        return baseResult.getData();
    }

    /**
     * 减少投资
     *
     * @param simpleAccount
     * @param combineCode
     * @param redeemRatio
     * @return
     */
    public ReduceInvestResp reduceInvest(SimpleAccount simpleAccount, String combineCode, BigDecimal redeemRatio) {
        ReduceInvestReq reduceInvestReq = new ReduceInvestReq();
        reduceInvestReq.setCombineCode(combineCode);
        reduceInvestReq.setRedeemRatio(redeemRatio);
        reduceInvestReq.setAllRedeemFlag(DataUtil.isOne(redeemRatio) ? FlagEnum.Y : FlagEnum.N);
        BaseResult<ReduceInvestResp> baseResult = tradeService.reduceInvest(simpleAccount, reduceInvestReq);
        if (!baseResult.isSuccess()) {
            log.error("reduceInvest t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            ReduceInvestResp reduceInvestResp = new ReduceInvestResp();
            reduceInvestResp.setError_no(DataUtil.strToInt(baseResult.getCode()));
            reduceInvestResp.setError_info(baseResult.getErrorMsg());
            return reduceInvestResp;
        }
        if (baseResult.getData() == null) {
            log.error("reduceInvest data is null");
            throw new BizException(BizErrorCodeEnum.REDUCE_INVEST_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("reduceInvest error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
        }
        return baseResult.getData();
    }

    /**
     * 客户组合解约
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    public CancelAgreementResp cancelAgreement(SimpleAccount simpleAccount, String combineCode) {
        CancelAgreementReq cancelAgreementReq = new CancelAgreementReq();
        cancelAgreementReq.setCombineCode(combineCode);
        BaseResult<CancelAgreementResp> baseResult = tradeService.cancelAgreement(simpleAccount, cancelAgreementReq);
        if (!baseResult.isSuccess()) {
            log.error("cancelAgreement t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            CancelAgreementResp cancelAgreementResp = new CancelAgreementResp();
            cancelAgreementResp.setError_no(DataUtil.strToInt(baseResult.getCode()));
            cancelAgreementResp.setError_info(baseResult.getErrorMsg());
            return cancelAgreementResp;
        }
        if (baseResult.getData() == null) {
            log.error("cancelAgreement data is null");
            throw new BizException(BizErrorCodeEnum.CANCEL_AGREEMENT_ERROR);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("cancelAgreement error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
        }
        return baseResult.getData();
    }

    /**
     * 修改目标盈续期方式
     *
     * @param simpleAccount
     * @param combineCode
     * @param stopProfitMode
     * @param operationalDealMode
     * @return
     */
    public ContractChangeResp changeProfitContract(SimpleAccount simpleAccount, String combineCode, StopProfitModeEnum stopProfitMode, OperationalDealModeEnum operationalDealMode) {
        ContractChangeReq contractChangeReq = new ContractChangeReq();
        contractChangeReq.setCombineCode(combineCode);
        contractChangeReq.setStopProfitMode(stopProfitMode.getCode());
        contractChangeReq.setOperationalDealMode(operationalDealMode.getCode());
        BaseResult<ContractChangeResp> baseResult = combStrategyTradeService.changeProfitContract(simpleAccount, contractChangeReq);
        if (!baseResult.isSuccess()) {
            log.error("changeProfitContract t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.MODIFY_TARGET_COMB_MODE_FAIL);
        }
        if (baseResult.getData() == null) {
            log.error("changeProfitContract data is null");
            throw new BizException(BizErrorCodeEnum.MODIFY_TARGET_COMB_MODE_FAIL);
        }
        if (!baseResult.getData().isSuccess()) {
            log.error("changeProfitContract error, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BizException(BizErrorCodeEnum.MODIFY_TARGET_COMB_MODE_FAIL, baseResult.getData().getError_info());
        }
        return baseResult.getData();
    }


    public QueryCombTagResp getCombTagInfo(SimpleAccount simpleAccount, String combineCode) {
        QueryCombTagReq queryCombTagReq = new QueryCombTagReq();
        queryCombTagReq.setCombineCode(combineCode);
        BaseResult<QueryCombTagResp> baseResult = combineService.queryCombTagInfo(simpleAccount, queryCombTagReq);
        if (!baseResult.isSuccess()) {
            log.error("getCombTagInfo t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.COMB_TAG_QUERY_ERROR);
        }

        if (baseResult.getData() == null || CollectionUtils.isEmpty(baseResult.getData().getRows())) {
            log.error("getCombTagInfo data is null");
            throw new BizException(BizErrorCodeEnum.COMB_TAG_QUERY_ERROR);
        }

        return baseResult.getData();
    }

    public boolean signTargetKycPaper(SimpleAccount simpleAccount, TargetKycAnswerReq answerReq) {
        BaseResult<TargetAnswerResp> baseResult = kycPaperService.signTargetKycPaper(simpleAccount, answerReq);
        if (!baseResult.isSuccess() || baseResult.getData() == null) {
            log.error("signTargetKycPaper t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(BizErrorCodeEnum.TARGET_KYC_SIGN_ERROR, TARGET_KYC_SIGN_ERROR.getDesc());
        }
        return Boolean.TRUE;
    }

    public QueryCopywritingExtResp queryStrategyTrait(SimpleAccount simpleAccount, String combineCode) {
        QueryCopywritingExtReq request = new QueryCopywritingExtReq();
        request.setCombineCode(combineCode);
        request.setEnCopywritingType(CopywritingTypeEnum.STRATEGY_TRAIT);
        BaseResult<QueryCopywritingExtResp> baseResult = combineService.queryCopywriting(simpleAccount, request);
        if (!baseResult.isSuccess()) {
            log.error("queryStrategyTrait t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            return null;
        }

        if (baseResult.getData() == null) {
            log.info("queryStrategyTrait combineCode={} data is null", combineCode);
            return null;
        }
        return baseResult.getData();
    }


    public List<BankInfoResp> getClientBankList(SimpleAccount simpleAccount) {
        BaseResult<List<BankInfoResp>> baseResult = bankService.queryClientBankInfo(simpleAccount, MONEY_TYPE);
        if (!baseResult.isSuccess()) {
            log.error("getClientBankList t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(QUERY_CLIENT_BANK_ERROR);
        }

        if (CollectionUtils.isEmpty(baseResult.getData())) {
            log.error("getClientBankList data is null");
            throw new BizException(QUERY_CLIENT_BANK_ERROR);
        }

        return baseResult.getData();
    }

    public AutoTransBankResp getAutoTransBank(SimpleAccount simpleAccount) {
        try {
            BaseResult<AutoTransBankResp> baseResult = bankService.queryAutoTransBank(simpleAccount);
            if (!baseResult.isSuccess()) {
                log.error("getAutoTransBank t2 error, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
                return null;
            }

            return baseResult.getData();
        } catch (Exception e) {
            log.error("getAutoTransBank t2 error, errorMsg={}", e);
            return null;
        }
    }

    public void signAutoTransContract(SimpleAccount simpleAccount, ContractSignReq contractSignReq) {
        try {
            BaseResult<ContractSignResponse> baseResult = combAutoTransService.signContract(simpleAccount, contractSignReq);
            if (baseResult.isSuccess() && baseResult.getData() != null) {
                log.info("signAutoTransContract success clientId {} combineCode {} serialNo {}", simpleAccount.getClientId(), contractSignReq.getCombineCode(), baseResult.getData().getSerial_no());
            } else {
                log.error("signAutoTransContract error clientId {} combineCode {}", simpleAccount.getClientId(), contractSignReq.getCombineCode(), baseResult.getErrorMsg());
            }
        } catch (Exception e) {
            log.error("signAutoTransContract error clientId {} combineCode {}", simpleAccount.getClientId(), contractSignReq.getCombineCode(), e);
        }
    }

    /**
     * 调用t2服务查询客户信息
     *
     * @param simpleAccount
     * @return
     */
    public T2ClientInfo getT2ClientInfo(SimpleAccount simpleAccount) {
        com.foundersc.ifc.t2.model.base.BaseResult<T2ClientInfo> baseResult = this.accountInfoService.getT2ClientInfo(castSimpleAccount(simpleAccount));
        if (!baseResult.isSuccess() || baseResult.getData() == null) {
            log.error("查询客户信息失败，clientId={}，code={}，errorMsg={}", simpleAccount.getClientId(),
                    baseResult.getCode(), baseResult.getErrorMsg());
            throw new BizException(QUERY_T2_CLIENT_INFO_ERROR, "查询客户信息失败");
        }
        return baseResult.getData();
    }

    /**
     * 类型转换
     *
     * @param simpleAccount
     * @return
     */
    public com.foundersc.ifc.t2.model.base.SimpleAccount castSimpleAccount(SimpleAccount simpleAccount) {
        if (simpleAccount == null) {
            return null;
        }
        com.foundersc.ifc.t2.model.base.SimpleAccount result = new com.foundersc.ifc.t2.model.base.SimpleAccount();
        BeanUtil.copyProperties(simpleAccount, result);
        return result;
    }
}
