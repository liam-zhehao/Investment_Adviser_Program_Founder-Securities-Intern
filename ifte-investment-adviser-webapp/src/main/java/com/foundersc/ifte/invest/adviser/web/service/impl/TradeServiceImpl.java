package com.foundersc.ifte.invest.adviser.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.foundersc.ifc.portfolio.t2.enums.v2.ErrorEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.FlagEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.bop.EpaperSignStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombCodeStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombShelfStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombRiskLimitTypeEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.elig.EligMatchFlag;
import com.foundersc.ifc.portfolio.t2.enums.v2.trade.AgreementStatusEnum;
import com.foundersc.ifc.portfolio.t2.enums.v2.trade.ExchTimeKindEnum;
import com.foundersc.ifc.portfolio.t2.model.v2.bop.EpaperDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.bop.EpaperSignSerialDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.trade.AgreementExtDTO;
import com.foundersc.ifc.portfolio.t2.model.v2.trade.ExchTimeExtDTO;
import com.foundersc.ifc.portfolio.t2.response.bank.AutoTransBankResp;
import com.foundersc.ifc.portfolio.t2.response.bank.BankInfoResp;
import com.foundersc.ifc.portfolio.t2.response.v2.bop.DownloadEpaperTemplateResp;
import com.foundersc.ifc.portfolio.t2.response.v2.bop.SignEpaperResp;
import com.foundersc.ifc.portfolio.t2.response.v2.elig.EligCheckResp;
import com.foundersc.ifc.portfolio.t2.response.v2.elig.QueryClientPreferExtResp;
import com.foundersc.ifc.portfolio.t2.response.v2.trade.*;
import com.foundersc.ifc.t2.model.responses.T2ClientInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombDealStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombProfitTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.TradeTimeCheckRes;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.TradeTimeRemoteService;
import com.foundersc.ifte.invest.adviser.web.annotation.IdempotentCheck;
import com.foundersc.ifte.invest.adviser.web.constants.CommonConstants;
import com.foundersc.ifte.invest.adviser.web.constants.T2ErrorCodes;
import com.foundersc.ifte.invest.adviser.web.constants.TipConstants;
import com.foundersc.ifte.invest.adviser.web.entity.CombRequestRecordEntity;
import com.foundersc.ifte.invest.adviser.web.enums.*;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.*;
import com.foundersc.ifte.invest.adviser.web.service.CombInfoService;
import com.foundersc.ifte.invest.adviser.web.service.TargetCombineModeService;
import com.foundersc.ifte.invest.adviser.web.service.TradeService;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.CommonFactory;
import com.foundersc.ifte.invest.adviser.web.service.impl.strategy.purchase.PurchaseStrategy;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.RecordAsyncWriter;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.T2ServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.service.impl.support.TradeCalendarRemoteServiceAdapter;
import com.foundersc.ifte.invest.adviser.web.util.*;
import com.foundersc.itc.product.model.TradeCalendar;
import com.foundersc.itc.product.service.TradeCalendarRemoteService;
import com.foundersc.itc.product.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.web.constants.CannotTradeTipConstants.*;
import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.*;
import static com.foundersc.ifte.invest.adviser.web.constants.T2ErrorCodes.NOT_ALLOW_REDUCE_39049;
import static com.foundersc.ifte.invest.adviser.web.constants.T2ErrorCodes.NOT_ALLOW_REDUCE_39234;
import static com.foundersc.ifte.invest.adviser.web.constants.TipConstants.*;
import static com.foundersc.ifte.invest.adviser.web.constants.UrlConstants.AUTO_TRANS_CONTRACT_URL;
import static com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum.COMB_PROFIT_TYPE_NOT_SUPPORT;
import static com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum.TRADE_TYPE_ERROR;
import static com.foundersc.ifte.invest.adviser.web.util.DateUtil.formatDate;

/**
 * @author wangfuwei
 * @date 2022/9/29
 */
@Service("tradeService")
@Slf4j
public class TradeServiceImpl implements TradeService {
    private final static String EXT_SERIAL_NO_SEPERATOR = "_";
    @Autowired
    private CombInfoService combInfoService;

    @Autowired
    private T2ServiceAdapter t2ServiceAdapter;

    @Autowired
    private TradeCalendarRemoteServiceAdapter tradeCalendarRemoteServiceAdapter;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TargetCombineModeService targetCombineModeService;

    @Autowired
    private TradeTimeRemoteService tradeTimeRemoteService;

    @Value("${url.epaper}")
    private String epaperUrl;

    @Autowired
    private RecordAsyncWriter recordAsyncWriter;

    @Value("${domain.xf}")
    private String urlPrefix;

    /**
     * 交易截止时间
     */
    private final static LocalTime tradeDeadTime = LocalTime.of(15, 0, 0);

    @Autowired
    private TradeCalendarRemoteService tradeCalendarRemoteService;

    /**
     * 快捷赎回比例保留两位小数，此处截断2位，按39133的配置参数来，0-取两位（不包括百分号），1-取4位
     */
    private final static int REDEEM_RATIO_SCALE = 4;

    /**
     * 判断非交易时间交易是当日生效还是下一交易日生效
     */
    private final static int CHECK_TRADE_TIME = 120000;

    /**
     * 购买时加锁的过期时间为2s
     */
    private final int PURCHASE_LOCK_EXPIRE_SECONDS = 2;

    @Override
    public TradePreCheckResultVO preCheck(String combineCode) {
        // 5. 组合交易时间检查，该期使用默认交易时间校验
        TradePreCheckResultVO tradePreCheckResultVO = checkTradeTime();
        if (!tradePreCheckResultVO.getCanTrade()) {
            return tradePreCheckResultVO;
        }
        TradePreCheckResultVO commonCheckResult = getTradePreCheckResultVO(combineCode);
        if (commonCheckResult != null) {
            return commonCheckResult;
        }
        return TradePreCheckResultVO.canTrade();
    }

    @Override
    public TradePreCheckResultVO newPreCheck(String combineCode, Integer tradeType) {
        if (!TradeTypeEnum.allTypeCodes.contains(tradeType)) {
            log.error("newPreCheck tradeType {} error", tradeType);
            throw new BizException(TRADE_TYPE_ERROR, TRADE_TYPE_ERROR.getDesc());
        }
        TradeTimeCheckRes timeCheckRes = tradeTimeRemoteService.checkTradeTime(ContextHolder.getSimpleAccount(), ContextHolder.getAppInfo().getVersion());
        //  不支持7*24小时，使用原校验
        if (!timeCheckRes.isSupport_7_24()) {
            return preCheck(combineCode);
        }
        //  组合交易时间检查，该期使用默认交易时间校验，只有赎回需要校验
        if (TradeTypeEnum.REDEEM.getTypeCode().equals(tradeType)) {
            TradePreCheckResultVO tradePreCheckResultVO = checkTradeTime();
            if (!tradePreCheckResultVO.getCanTrade()) {
                return tradePreCheckResultVO;
            }
        }
        TradePreCheckResultVO commonCheckResult = getTradePreCheckResultVO(combineCode, tradeType);
        if (commonCheckResult != null) {
            return commonCheckResult;
        }

        return TradePreCheckResultVO.canTrade();
    }

    private TradePreCheckResultVO getTradePreCheckResultVO(String combineCode) {
        CombineInfoVO combineInfo = combInfoService.info(combineCode, DateUtil.dateToInt(new Date()));
        // 目标盈不允许在老版本做转入转出转出操作
        if (combineInfo.isTargetComb()) {
            if (!AppVersionUtil.isAppVersionEnough(TARGET_VERSION, ContextHolder.getAppInfo().getVersion())) {
                log.warn("preCheck target version {} app version {}", TARGET_VERSION, ContextHolder.getAppInfo().getVersion());
                return TradePreCheckResultVO.cannotTrade(USE_NEW_VERSION_TIP);
            }
        }

        // 1. 组合状态
        if (!CombCodeStatusEnum.VALID.getCode().equals(combineInfo.getCombCodeStatus())) {
            log.warn("combCodeStatus not valid");
            return TradePreCheckResultVO.cannotTrade(COMB_CODE_STATUS_ERROR);
        }
        // 2. 组合上下架状态
        if (!CombShelfStatusEnum.ON_SHELF.getCode().equals(combineInfo.getCombShelfStatus())) {
            log.warn("combShelfStatus not no shelf");
            return TradePreCheckResultVO.cannotTrade(COMB_SHELF_STATUS_ERROR);
        }
        // 6. 是否解约中
        if (isCancellingComb(combineCode)) {
            log.warn("combine is cancelling, not allow trade");
            return TradePreCheckResultVO.cannotTrade(CANCELLING_NOT_ALLOW_TRADE);
        }
        return null;
    }

    private TradePreCheckResultVO getTradePreCheckResultVO(String combineCode, Integer tradeType) {
        CombineInfoVO combineInfo = combInfoService.info(combineCode, DateUtil.dateToInt(new Date()));
        // 1. 组合状态
        if (!CombCodeStatusEnum.VALID.getCode().equals(combineInfo.getCombCodeStatus())) {
            log.warn("combCodeStatus not valid");
            return TradePreCheckResultVO.cannotTrade(COMB_CODE_STATUS_ERROR);
        }
        // 2. 组合上下架状态
        if (!CombShelfStatusEnum.ON_SHELF.getCode().equals(combineInfo.getCombShelfStatus())) {
            log.warn("combShelfStatus not no shelf");
            return TradePreCheckResultVO.cannotTrade(COMB_SHELF_STATUS_ERROR);
        }
        // 6. 是否解约中
        if (isCancellingComb(combineCode)) {
            log.warn("combine is cancelling, not allow trade");
            return TradePreCheckResultVO.cannotTrade(CANCELLING_NOT_ALLOW_TRADE);
        }

        // 目标盈判断是否可购买
        if (combineInfo.isTargetComb() && TradeTypeEnum.PURCHASE.getTypeCode().equals(tradeType)) {
            if (BoolEnum.NO.getId().equals(canTargetCombPurchase(combineInfo))) {
                log.warn("target combine is not allow trade");
                return TradePreCheckResultVO.cannotTrade(TARGET_COMB_CAN_NOT_PURCHASE);
            }
        }
        return null;
    }

    /**
     * 检查是否在交易时间中
     *
     * @param combineCode
     * @return
     */
    private TradePreCheckResultVO checkTradeTime(String combineCode) {
        boolean hasSignedComb = hasSignedComb(combineCode);
        ExchTimeKindEnum exchTimeKind = hasSignedComb ? ExchTimeKindEnum.TRADE_APPLY : ExchTimeKindEnum.SIGN_AGREEMENT;
        QueryExchTimeResp queryExchTimeResp = t2ServiceAdapter.queryExchTime(ContextHolder.getSimpleAccount(), exchTimeKind);
        ExchTimeExtDTO exchTimeExt = queryExchTimeResp.getRows().get(0);
        log.info("hasSignedComb={}, exchTimeExt={}", hasSignedComb, exchTimeExt);
        // 时间格式HHmmss
        LocalTime beginTime = DateUtil.intToLocalTime(exchTimeExt.getBegin_time());
        LocalTime endTime = DateUtil.intToLocalTime(exchTimeExt.getEnd_time());
        boolean inTradeTime = isInTradeTime(beginTime, endTime);
        return inTradeTime ? TradePreCheckResultVO.canTrade() :
                TradePreCheckResultVO.cannotTrade(String.format(ALLOW_TRADE_TIME, beginTime, endTime));
    }

    /**
     * 校验交易时间
     *
     * @return
     */
    private TradePreCheckResultVO checkTradeTime() {
        TradeTimeCheckRes tradeTimeCheckRes = tradeTimeRemoteService.checkTradeTime(ContextHolder.getSimpleAccount(), ContextHolder.getAppInfo().getVersion());
        boolean support7_24 = tradeTimeCheckRes.isSupport_7_24();
        boolean inTradeTime = tradeTimeCheckRes.isInTradeTime();
        log.info("check trade time, support_7_24={}, inTradeTime={}", support7_24, inTradeTime);
        // 如果支持7*24或在交易时间，则可以交易
        TradePreCheckResultVO resultVO = (support7_24 || inTradeTime) ? TradePreCheckResultVO.canTrade() : TradePreCheckResultVO.cannotTrade(tradeTimeCheckRes.getTip());
        resultVO.setInTradeTime(inTradeTime);
        return resultVO;
    }

    /**
     * 是否在交易时间中
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    private boolean isInTradeTime(LocalTime beginTime, LocalTime endTime) {
        TradeCalendar tradeCalendar = tradeCalendarRemoteServiceAdapter.queryTradeDate(new Date());
        if (!tradeCalendar.isOpenMarket()) {
            return false;
        }
        LocalTime now = LocalTime.now();
        return (now.isAfter(beginTime) || now.equals(beginTime)) && now.isBefore(endTime);
    }

    @Override
    public boolean hasSignedComb(String combineCode) {
        QueryAgreementResp queryAgreementResp = t2ServiceAdapter.queryAgreement(ContextHolder.getSimpleAccount(), combineCode);
        // 返回的协议中有【已生效】，表示客户组合已签约，签约之后协议立刻为【已生效】
        boolean hasSigned = hasSignedComb(queryAgreementResp);
        log.info("hasSigned={}", hasSigned);
        return hasSigned;
    }

    @Override
    public Integer canTargetCombPurchase(CombineInfoVO combineInfo) {
        CombDealStatusEnum statusEnum = CombDealStatusEnum.parseByStatusCode(combineInfo.getDealStatus());
        if (statusEnum != CombDealStatusEnum.PURCHASE) {
            return BoolEnum.NO.getId();
        }
        TradeTimeCheckRes timeCheckRes = tradeTimeRemoteService.checkTradeTime(ContextHolder.getSimpleAccount(), ContextHolder.getAppInfo().getVersion());
        if (!timeCheckRes.isSupport_7_24()) {
            log.info("canTargetCombPurchase combine {} can not trade all time.", combineInfo.getCombineCode());
            return timeCheckRes.isInTradeTime() ? BoolEnum.YES.getId() : BoolEnum.NO.getId();
        }

        if (StringUtils.isEmpty(combineInfo.getBuyStartTime()) ||
                StringUtils.isEmpty(combineInfo.getBuyEndDate())) {
            return BoolEnum.YES.getId();
        }
        Date startTime = DateUtils.parseDate(combineInfo.getBuyStartTime(), DateUtils.YYYYMMDDHHMMSS);
        Date endTime = DateUtils.parseDate(combineInfo.getBuyEndTime(), DateUtils.YYYYMMDDHHMMSS);
        Date currentDate = new Date();
        if (currentDate.after(startTime) && currentDate.before(endTime)) {
            return BoolEnum.YES.getId();
        }
        return BoolEnum.NO.getId();
    }

    /**
     * 是否存在指定状态的协议
     *
     * @param queryAgreementResp
     * @param agreementStatus
     * @return
     */
    private boolean isAgreementExists(QueryAgreementResp queryAgreementResp, AgreementStatusEnum agreementStatus) {
        return queryAgreementResp.getRows().stream()
                .filter(x -> agreementStatus.getCode().equals(x.getAgreement_status()))
                .findAny().isPresent();
    }

    @Override
    public boolean hasSignedComb(QueryAgreementResp queryAgreementResp) {
        return isAgreementExists(queryAgreementResp, AgreementStatusEnum.VALID);
    }

    @Override
    public boolean isCancellingComb(String combineCode) {
        QueryAgreementResp queryAgreementResp = t2ServiceAdapter.queryAgreement(ContextHolder.getSimpleAccount(), combineCode);
        return isCancellingComb(queryAgreementResp);
    }

    @Override
    public boolean isCancellingComb(QueryAgreementResp queryAgreementResp) {
        return isAgreementExists(queryAgreementResp, AgreementStatusEnum.CANCELING);
    }

    @Override
    public EligibilityCheckResultVO checkEligibility(String combineCode) {
        T2ClientInfo t2ClientInfo = t2ServiceAdapter.getT2ClientInfo(ContextHolder.getSimpleAccount());
        if (isIdCardOverdue(t2ClientInfo)) {
            // 身份证过期直接返回
            return EligibilityCheckResultVO.builder().idCardOverdue(true).build();
        }
        if (!hasEvalRiskLevel(t2ClientInfo)) {
            // 未进行风险测评直接返回
            return EligibilityCheckResultVO.builder().hasRiskLevelEval(false).build();
        }
        EligCheckResp eligCheckResp = t2ServiceAdapter.checkElig(ContextHolder.getSimpleAccount(), combineCode);
        if (eligCheckResp.isSuccess()) {
            EligMatchFlag eligRiskFlag = EligMatchFlag.parseByCode(eligCheckResp.getElig_risk_flag());
            EligMatchFlag eligInvestKindFlag = EligMatchFlag.parseByCode(eligCheckResp.getElig_investkind_flag());
            EligMatchFlag eligInvestTermFlag = EligMatchFlag.parseByCode(eligCheckResp.getElig_term_flag());
            if (CombRiskLimitTypeEnum.ALLOW_TRADE.getCode().equals(eligCheckResp.getComb_risk_limit_type())) {
                // 以下包括专业投资者适当性豁免，elig_risk_flag、elig_investkind_flag和elig_term_flag都是空，使用comb_risk_limit_type为2来判断不提示弹窗
                log.info("comb_risk_limit_type={}, allow trade", eligCheckResp.getComb_risk_limit_type());
                eligRiskFlag = EligMatchFlag.MATCH;
                eligInvestKindFlag = EligMatchFlag.MATCH;
                eligInvestTermFlag = EligMatchFlag.MATCH;
            }
            return EligibilityCheckResultVO.builder()
                    .riskExpire(FlagEnum.Y.getCode().equals(eligCheckResp.getExpire_risk_flag()))
//                    .riskLevelNeedForceMatch(eligRiskFlag.getNeedForceMatch())
                    // 本期风险等级默认强匹配
                    .riskLevelNeedForceMatch(true)
                    .riskLevelMatch(EligMatchFlag.MATCH.equals(eligRiskFlag))
//                    .investKindNeedForceMatch(eligInvestKindFlag.getNeedForceMatch())
                    // 本期投资品种默认弱匹配
                    .investKindNeedForceMatch(false)
                    .investKindMatch(EligMatchFlag.MATCH.equals(eligInvestKindFlag))
//                    .investTermNeedForceMatch(eligInvestTermFlag.getNeedForceMatch())
                    // 本期投资期限默认弱匹配
                    .investTermNeedForceMatch(false)
                    .investTermMatch(EligMatchFlag.MATCH.equals(eligInvestTermFlag))
                    .clientRiskLevel(eligCheckResp.getCorp_risk_level())
                    .combRiskLevel(eligCheckResp.getComb_risk_level())
                    .clientEnInvestKind(eligCheckResp.getEn_invest_kind())
                    .combInvestKind(eligCheckResp.getComb_invest_kind())
                    .clientEnInvestTerm(eligCheckResp.getEn_invest_term())
                    .combInvestTerm(eligCheckResp.getComb_invest_term())
                    .idCardOverdue(false)
                    .hasRiskLevelEval(true)
                    .combRiskLimitType(eligCheckResp.getComb_risk_limit_type())
                    .build();
        } else if (forceMatchFail(eligCheckResp)) {
            log.warn("eligCheck forceMatchFail, errorNo={}, errorInfo={}", eligCheckResp.getError_no(), eligCheckResp.getError_info());
            CombineInfoVO combineInfo = combInfoService.info(combineCode, DateUtil.dateToInt(new Date()));
            QueryClientPreferExtResp clientPreferExt = t2ServiceAdapter.queryClientPreferExt(ContextHolder.getSimpleAccount());
            EligibilityCheckResultVO result = EligibilityCheckResultVO.builder()
                    .riskExpire(FlagEnum.Y.getCode().equals(clientPreferExt.getExceed_risk_flag()))
                    .riskLevelNeedForceMatch(true)
                    .riskLevelMatch(true)
                    .clientRiskLevel(clientPreferExt.getCorp_risk_level().toString())
                    .combRiskLevel(combineInfo.getCombRiskLevel())
                    .clientEnInvestKind(clientPreferExt.getEn_invest_kind())
                    .combInvestKind(combineInfo.getCombInvestKind() == null ? StringUtils.EMPTY : combineInfo.getCombInvestKind())
                    .clientEnInvestTerm(clientPreferExt.getEn_invest_term())
                    .combInvestTerm(combineInfo.getCombInvestTerm() == null ? StringUtils.EMPTY : combineInfo.getCombInvestTerm())
                    .investTermNeedForceMatch(true)
                    .investKindNeedForceMatch(true)
                    .idCardOverdue(false)
                    .hasRiskLevelEval(true)
                    .build();
            if (investTermForceMatchFail(eligCheckResp)) {
                result.setInvestTermMatch(false);
                result.setInvestKindMatch(true);
            } else {
                result.setInvestTermMatch(true);
                result.setInvestKindMatch(false);
            }
            return result;
        } else {
            log.error("eligCheck failed, errorNo={}, errorInfo={}", eligCheckResp.getError_no(), eligCheckResp.getError_info());
            throw new BizException(BizErrorCodeEnum.ELIG_CHECK_ERROR, eligCheckResp.getError_info());
        }
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
     * 强匹配失败
     *
     * @param eligCheckResp
     * @return
     */
    private boolean forceMatchFail(EligCheckResp eligCheckResp) {
        return investKindForceMatchFail(eligCheckResp) || investTermForceMatchFail(eligCheckResp);
    }

    /**
     * 投资品种强匹配失败
     *
     * @param eligCheckResp
     * @return
     */
    private boolean investTermForceMatchFail(EligCheckResp eligCheckResp) {
        return ErrorEnum.ERR_IAS_TERMFLAG_NOMATCH.getErrorNo().equals(eligCheckResp.getError_no());
    }

    /**
     * 投资品种强匹配失败
     *
     * @param eligCheckResp
     * @return
     */
    private boolean investKindForceMatchFail(EligCheckResp eligCheckResp) {
        return ErrorEnum.ERR_IAS_INVESTKIND_NOMATCH.getErrorNo().equals(eligCheckResp.getError_no());
    }

    @Override
    public PurchaseInfoVO getPurchaseInfo(String combineCode) {
        PurchaseInfoVO result = new PurchaseInfoVO();
        int initDate = DateUtil.dateToInt(new Date());
        CombineInfoVO combineInfo = combInfoService.info(combineCode, initDate);
        BeanUtil.copyProperties(combineInfo, result);
        result.setNeedSign(!hasSignedComb(combineCode));
        // 快捷输入
        result.setFastAmountTags(getFastAmountTag(result.getMinBuyBalance()));
        // 账户余额
        QueryEnableBalanceResp enableBalance = t2ServiceAdapter.queryEnableBalance(ContextHolder.getSimpleAccount());
        result.setEnableBalance(DataUtil.strToBigDecimal(enableBalance.getEnable_balance()));
        // 转入文案提示
        List<String> purchaseCopywritings = combInfoService.getPurchaseCopywritings(combineCode, getInitDate(LocalDateTime.now()));
        TradePreCheckResultVO tradePreCheckResultVO = checkTradeTime();
        if (!tradePreCheckResultVO.getInTradeTime()) {
            purchaseCopywritings.add(TipConstants.NON_TRADE_TIME_TIP);
        }
        result.setInTradeTime(tradePreCheckResultVO.getInTradeTime() ? BoolEnum.YES.getId() : BoolEnum.NO.getId());
        result.setPurchaseCopywritings(purchaseCopywritings);
        result.setEpaperUrl(String.format(epaperUrl, combineCode));
        if (combineInfo.isTargetComb()) {
            result.setTargetCombMode(targetCombineModeService.queryModeInfoByInvestOrganNo(combineCode, combineInfo.getInvestOrganNo()));
        }
        return result;
    }

    /**
     * 快捷输入金额: 起购金额、起购金额的10倍、起购金额的20倍
     *
     * @param minBalance
     * @return
     */
    private List<FastAmountTag> getFastAmountTag(BigDecimal minBalance) {
        List<FastAmountTag> result = new ArrayList<>();
        result.add(new FastAmountTag(minBalance));
        result.add(new FastAmountTag(minBalance.multiply(new BigDecimal(10)).setScale(0, RoundingMode.HALF_UP)));
        result.add(new FastAmountTag(minBalance.multiply(new BigDecimal(20)).setScale(0, RoundingMode.HALF_UP)));
        return result;
    }

    @Override
    // @Cacheable(cacheNames = EPAPER, key = "#combineCode", unless = "#result == null")
    public List<EpaperVO> listEpaper(String combineCode) {
        List<EpaperDTO> epaperList = t2ServiceAdapter.queryEpaper(ContextHolder.getSimpleAccount(), combineCode);
        List<EpaperVO> result = new ArrayList<>();
        for (EpaperDTO epaperDTO : epaperList) {
            EpaperVO epaperVO = new EpaperVO(epaperDTO);
            DownloadEpaperTemplateResp downloadEpaperTemplateResp = t2ServiceAdapter.downloadEpaperTemplate(ContextHolder.getSimpleAccount(), epaperVO.getTemplateId(), epaperVO.getVersionNo());
            epaperVO.setImageData(downloadEpaperTemplateResp.getImage_data());
            epaperVO.setFileType(downloadEpaperTemplateResp.getFile_type());
            result.add(epaperVO);
        }
        return result;
    }

    @Override
    @IdempotentCheck(type = IdemCheckTypeEnum.SIGN_EPAPER, checkKey = IdemCheckKeyEnum.CLIENT_ID)
    public Boolean signEpaper(String combineCode) {
        List<EpaperVO> epaperList = tradeService.listEpaper(combineCode);
        if (CollectionUtils.isEmpty(epaperList)) {
            log.warn("signEpaper fail, epaperList is empty");
            return false;
        }
        log.info("epaperList size={}", epaperList.size());
        if (hasSignedEpaper(combineCode)) {
            return true;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("sign epaper");
        for (EpaperVO epaperVO : epaperList) {
            String extSerialNo = getExtSerialNo(combineCode, epaperVO);
            SignEpaperResp signEpaperResp = t2ServiceAdapter.signEpaper(ContextHolder.getSimpleAccount(), combineCode, String.valueOf(epaperVO.getTemplateId()),
                    extSerialNo, epaperVO.getVersionNo(), FlagEnum.N);
            log.info("signed epaper, combineCode={}, templateId={}, versionNo={}, extSerialNo={}, result epaper_id={}",
                    combineCode, epaperVO.getTemplateId(), epaperVO.getVersionNo(), extSerialNo, signEpaperResp.getEpaper_id());
        }
        stopWatch.stop();
        log.info(LogUtil.stopWatchLog(stopWatch));
        return true;
    }

    /**
     * 生成外部流水号
     *
     * @param epaperVO
     * @param combineCode
     * @return
     */
    private String getExtSerialNo(String combineCode, EpaperVO epaperVO) {
        String originExtSerialNo = StringUtils.joinWith(EXT_SERIAL_NO_SEPERATOR, ContextHolder.getSimpleAccount().getClientId(),
                combineCode, epaperVO.getTemplateId(), epaperVO.getTemplateType(),
                DateUtils.formatDate(new Date(), CommonConstants.LONG_TIME_FORMAT));
        log.info("originExtSerialNo={}", originExtSerialNo);
        return DigestUtils.md5Hex(originExtSerialNo);
    }

    /**
     * 组合信息
     *
     * @param combineCode
     * @return
     */
    private CombineInfoVO getCombineInfo(String combineCode) {
        return combInfoService.info(combineCode, DateUtil.intDate());
    }

    @Override
    @IdempotentCheck(type = IdemCheckTypeEnum.PURCHASE, checkKey = IdemCheckKeyEnum.CLIENT_ID)
    public PurchaseResultVO purchase(PurchaseReq purchaseReq) {
        CombineInfoVO combineInfo = combInfoService.info(purchaseReq.getCombineCode(), DateUtil.intDate());
        PurchaseStrategy purchaseStrategy = CommonFactory.getPurchaseTemplate(CombProfitTypeEnum.parseByTypeId(combineInfo.getCombProfitType()));
        if (purchaseStrategy == null) {
            log.error("comb purchase {} current profit type {} not support", purchaseReq.getCombineCode(), combineInfo.getCombProfitType());
            throw new BizException(COMB_PROFIT_TYPE_NOT_SUPPORT);
        }
        return purchaseStrategy.tradeProcess(purchaseReq, combineInfo);
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

    @Override
    @IdempotentCheck(type = IdemCheckTypeEnum.REVOKE, checkKey = IdemCheckKeyEnum.CLIENT_ID)
    public RevokeResultVO revoke(RevokeReq revokeReq) {
        CombineInfoVO combineInfo = getCombineInfo(revokeReq.getCombineCode());
        RecallCombRequestResp recallCombRequestResp;
        if (combineInfo.isTargetComb()) {
            // 目标盈撤单
            recallCombRequestResp = t2ServiceAdapter.recallTargetCombRequest(ContextHolder.getSimpleAccount(), revokeReq.getOrigCombRequestNo());
        } else {
            recallCombRequestResp = t2ServiceAdapter.recallCombRequest(ContextHolder.getSimpleAccount(), revokeReq.getOrigCombRequestNo());
        }
        RevokeResultVO result = new RevokeResultVO();
        result.setCombRequestNo(recallCombRequestResp.getComb_request_no());
        CombRequestRecordEntity combRequestRecord = build(revokeReq, combineInfo.getCombineName(), recallCombRequestResp);
        // 记录数据库
        recordAsyncWriter.asyncSaveCombRequestRecord(combRequestRecord);
        if (!recallCombRequestResp.isSuccess()) {
            if (T2ErrorCodes.NOT_ALLOW_RECALL.equals(recallCombRequestResp.getError_no())) {
                throw new BizException(BizErrorCodeEnum.NOT_ALLOW_RECALL_COMB_REQUEST, recallCombRequestResp.getError_info());
            }
            throw new BizException(BizErrorCodeEnum.RECALL_COMB_REQUEST_ERROR);
        }
        return result;
    }

    @Override
    public RedeemInfoVO getRedeemInfo(String combineCode) {
        RedeemInfoVO result = new RedeemInfoVO();
        int initDate = DateUtil.dateToInt(new Date());
        CombineInfoVO combineInfo = combInfoService.info(combineCode, initDate);
        BeanUtil.copyProperties(combineInfo, result);
        QueryAssetFetchExtResp queryAssetFetchExtResp = t2ServiceAdapter.queryAssetFetchExt(ContextHolder.getSimpleAccount(), combineCode);
        result.setMinFetchRatio(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getMin_fetch_ratio()));
        result.setMaxFetchRatio(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getMax_fetch_ratio()));
        result.setMinFetchBalance(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getMin_fetch_balance()));
        result.setMaxFetchBalance(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getMax_fetch_balance()));
        result.setAllowAllRedeem(FlagEnum.Y.getCode().equals(queryAssetFetchExtResp.getAllow_all_redeem_flag()));
        result.setAllRedeemRatio(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getAll_redeem_ratio()));
        result.setEnRedeem(canRedeem(queryAssetFetchExtResp));
        result.setCombAsset(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getCombi_asset()));
        if (combineInfo.getCalAdviseFee() != null) {
            result.setPredictRedeemAsset(result.getCombAsset().multiply(BigDecimal.ONE.subtract(combineInfo.getCalAdviseFee())).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        result.setFastRatioTags(getFastRatioTags(result.getMinFetchRatio(), result.getMaxFetchRatio(), result.getAllowAllRedeem(), result.getAllRedeemRatio()));
        result.setRedeemTip(getRedeemTip(result.getMinFetchRatio(), result.getMaxFetchRatio(), result.getAllowAllRedeem(), result.getAllRedeemRatio()));
        result.setRedeemCopyWrites(combInfoService.getRedeemCopywritings(combineCode, getInitDate(LocalDateTime.now())));
        return result;
    }

    /**
     * localDateTime，15:00之前传当天，15:00之后传第二天
     *
     * @param localDateTime 当前日期时间
     * @return
     */
    private Integer getInitDate(LocalDateTime localDateTime) {
        if (isAfterDeadTime(localDateTime)) {
            // 15:00之后取下一个交易日（不含当天）
            LocalDateTime nextLocalDateTime = localDateTime.plusDays(1);
            Date date = Date.from(nextLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            return tradeCalendarRemoteService.queryNextTradeDate(date);
        } else {
            // 15:00之前取下一个交易日（含当天）
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            return tradeCalendarRemoteService.queryNextTradeDate(date);
        }
    }

    /**
     * 是否为15:00之后
     *
     * @return
     */
    private boolean isAfterDeadTime(LocalDateTime localDateTime) {
        return LocalTime.from(localDateTime).isAfter(tradeDeadTime);
    }

    /**
     * 是否允许赎回
     *
     * @param queryAssetFetchExtResp
     * @return
     */
    private boolean canRedeem(QueryAssetFetchExtResp queryAssetFetchExtResp) {
        // 恒生回复说没有用en_redeem_flag和allow_all_redeem_flag标识判断，此处先注释掉
//        if (FlagEnum.Y.getCode().equals(queryAssetFetchExtResp.getEn_redeem_flag())
//                && DataUtil.isPositive(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getMax_fetch_ratio()))) {
//            return true;
//        }
        if (DataUtil.isPositive(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getMax_fetch_ratio()))) {
            return true;
        }
//        if (FlagEnum.Y.getCode().equals(queryAssetFetchExtResp.getAllow_all_redeem_flag())
//                && DataUtil.isPositive(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getAll_redeem_ratio()))) {
//            return true;
//        }
        if (DataUtil.isPositive(DataUtil.strToBigDecimal(queryAssetFetchExtResp.getAll_redeem_ratio()))) {
            return true;
        }
        return false;
    }

    /**
     * 快捷赎回：可转出下限、可转出上限的50%、全部
     *
     * @param minFetchRatio
     * @param maxFetchRatio
     * @param allowAllRedeem
     * @param allRedeemRatio
     * @return
     */
    private List<FastAmountTag> getFastRatioTags(BigDecimal minFetchRatio, BigDecimal maxFetchRatio, boolean allowAllRedeem, BigDecimal allRedeemRatio) {
        List<FastAmountTag> result = new ArrayList<>();
        // 对于minFetchRatio用RoundingMode.CEILING，防止四舍五入使得值小于minFetchRatio（正常不会出现）
        BigDecimal minFastRatio = minFetchRatio.setScale(REDEEM_RATIO_SCALE, RoundingMode.CEILING);
        if (DataUtil.isPositive(minFastRatio)) {
            result.add(new FastAmountTag(minFastRatio));
        }
        // 对于maxFetchRatio用RoundingMode.FLOOR，防止四舍五入使得值大于maxFetchRatio（正常不会出现）
        BigDecimal maxFastRatio = maxFetchRatio.setScale(REDEEM_RATIO_SCALE, RoundingMode.FLOOR);
        BigDecimal halfMax = maxFastRatio.divide(new BigDecimal(2)).setScale(REDEEM_RATIO_SCALE, RoundingMode.HALF_UP);
        // 只有halfMax介于minFastRatio和maxFastRatio才会加入result
        if (halfMax.compareTo(minFastRatio) > 0 && halfMax.compareTo(maxFastRatio) < 0) {
            result.add(new FastAmountTag(halfMax));
        }
//        if (allowAllRedeem && DataUtil.isPositive(allRedeemRatio) && !DataUtil.isOne(minFastRatio)) {
        if (DataUtil.isPositive(allRedeemRatio) && !DataUtil.isOne(minFastRatio)) {
            result.add(new FastAmountTag(BigDecimal.ONE));
        }
        // 将value处理成整数，方便客户端处理
        for (FastAmountTag fastAmountTag : result) {
            fastAmountTag.setValue(fastAmountTag.getValue().multiply(ONE_HUNDRED));
        }
        log.info("minFetchRatio={}, maxFetchRatio={}, allowAllRedeem={}, result={}", minFetchRatio, maxFetchRatio, allowAllRedeem, result);
        return result;
    }

    /**
     * 赎回比例提示
     *
     * @param minFetchRatio
     * @param maxFetchRatio
     * @param allowAllRedeem
     * @param allRedeemRatio
     * @return
     */
    private static String getRedeemTip(BigDecimal minFetchRatio, BigDecimal maxFetchRatio, boolean allowAllRedeem, BigDecimal allRedeemRatio) {
        String pattern;
//        if (DataUtil.isZero(maxFetchRatio) && (!allowAllRedeem || !DataUtil.isPositive(allRedeemRatio))) {
        if (DataUtil.isZero(maxFetchRatio) && (!DataUtil.isPositive(allRedeemRatio))) {
            return NOT_ALLOW_REDEEM;
        }
        if (minFetchRatio.equals(maxFetchRatio)) {
            if (DataUtil.isOne(maxFetchRatio)) {
                pattern = REDEEM_RATIO_TIP_4;
            } else if (DataUtil.isPositive(allRedeemRatio)) {
                if (DataUtil.isZero(maxFetchRatio)) {
                    // 防止出现可转出0%或全部
                    pattern = REDEEM_RATIO_TIP_4;
                } else {
                    pattern = REDEEM_RATIO_TIP_3;
                }
            } else {
                pattern = REDEEM_RATIO_TIP_6;
            }
        } else {
            if (DataUtil.isOne(maxFetchRatio)) {
                pattern = REDEEM_RATIO_TIP_5;
            } else if (DataUtil.isPositive(allRedeemRatio)) {
                pattern = REDEEM_RATIO_TIP_2;
            } else {
                pattern = REDEEM_RATIO_TIP_1;
            }
        }
        return String.format(pattern, RatioUtil.formatPercent(minFetchRatio, getFractionDigit(minFetchRatio), RoundingMode.FLOOR),
                RatioUtil.formatPercent(maxFetchRatio, getFractionDigit(maxFetchRatio), RoundingMode.FLOOR));
    }

    /**
     * 百分比的小数位数
     *
     * @param b
     * @return
     */
    private static int getFractionDigit(BigDecimal b) {
        return DataUtil.getDotNum(b.multiply(ONE_HUNDRED), 2);
    }

    @Override
    @IdempotentCheck(type = IdemCheckTypeEnum.REVOKE, checkKey = IdemCheckKeyEnum.CLIENT_ID)
    public RedeemResultVO redeem(RedeemReq redeemReq) {
        CombineInfoVO combineInfo = combInfoService.info(redeemReq.getCombineCode(), DateUtil.intDate());
        // 赎回前检查
        TradePreCheckResultVO preCheckResult = preCheck(redeemReq.getCombineCode());
        if (!preCheckResult.getCanTrade()) {
            log.warn("redeem fail, cannot trade for {}", preCheckResult.getCannotTradeTip());
            throw new BizException(BizErrorCodeEnum.REDEEM_ERROR, preCheckResult.getCannotTradeTip());
        }
        RedeemInfoVO redeemInfo = getRedeemInfo(redeemReq.getCombineCode());
        if (!redeemInfo.getEnRedeem()) {
            log.warn("redeem fail, not allow redeem");
            throw new BizException(BizErrorCodeEnum.REDEEM_ERROR, NOT_ALLOW_REDEEM);
        }
        // 是否全部赎回
        boolean redeemAll = isRedeemAll(redeemReq.getRedeemRatio());
        if (redeemAll) {
            // 恒生没用allow_all_redeem_flag标识，此处暂时注释掉
//            if (!redeemInfo.getAllowAllRedeem() || !DataUtil.isPositive(redeemInfo.getAllRedeemRatio())) {
            if (!DataUtil.isPositive(redeemInfo.getAllRedeemRatio())) {
                log.warn("redeem fail, redeem ratio={}, allowAllRedeem={}", redeemReq.getRedeemRatio(), redeemInfo.getAllowAllRedeem());
                throw new BizException(BizErrorCodeEnum.REDEEM_ERROR, NOT_ALLOW_REDEEM_ALL);
            }
            if (!canCancelAgreement(redeemReq.getCombineCode())) {
                log.warn("redeem fail, cannot cancel agreement");
                throw new BizException(BizErrorCodeEnum.REDEEM_ERROR, NOT_ALLOW_REDEEM_ALL);
            }
        } else {
            // 校验赎回比例
            if (redeemReq.getRedeemRatio().compareTo(redeemInfo.getMinFetchRatio()) < 0) {
                log.warn("redeem fail, redeemRatio={} less than minFetchRatio={}", redeemReq.getRedeemRatio(), redeemInfo.getMinFetchRatio());
                throw new BizException(BizErrorCodeEnum.REDEEM_ERROR, LESS_THAN_MIN_RATIO);
            }
            if (redeemReq.getRedeemRatio().compareTo(redeemInfo.getMaxFetchRatio()) > 0) {
                log.warn("redeem fail, redeemRatio={} more than maxFetchRatio={}", redeemReq.getRedeemRatio(), redeemInfo.getMaxFetchRatio());
                throw new BizException(BizErrorCodeEnum.REDEEM_ERROR, MORE_THAN_MAX_RAIO);
            }
        }
        if (redeemAll) {
            return redeemAll(redeemReq, combineInfo);
        } else {
            return reduceInvest(redeemReq, combineInfo);
        }
    }

    /**
     * 全部赎回
     *
     * @return
     */
    private RedeemResultVO redeemAll(RedeemReq redeemReq, CombineInfoVO combineInfo) {
        log.info("redeemAll, cancel agreement");
        CancelAgreementResp cancelAgreementResp = t2ServiceAdapter.cancelAgreement(ContextHolder.getSimpleAccount(), redeemReq.getCombineCode());
        CombRequestRecordEntity combRequestRecord = build(redeemReq, combineInfo.getCombineName(), cancelAgreementResp);
        // 记录数据库
        recordAsyncWriter.asyncSaveCombRequestRecord(combRequestRecord);
        if (!cancelAgreementResp.isSuccess()) {
            log.warn("存在追加投资在途业务，解约失败，尝试减少投资");
            return reduceInvest(redeemReq, combineInfo);
        }
        RedeemResultVO resultVO = new RedeemResultVO(cancelAgreementResp.getComb_request_no(), cancelAgreementResp.getRequest_no(), REDEEM_RESULT_TITLE, PURCHASE_RESULT_TIP);
        String currentTime = DateUtils.formatDate(new Date(), DEFAULT_DATE_TIME_FORMAT);
        resultVO.setFlowList(RedeemResultVO.getTradeTimeFlow(currentTime, cancelAgreementResp.getPre_affirm_date().toString(), cancelAgreementResp.getPre_arrive_date().toString()));
        return resultVO;
    }

    /**
     * 减少投资
     *
     * @param redeemReq
     * @param combineInfo
     * @return
     */
    private RedeemResultVO reduceInvest(RedeemReq redeemReq, CombineInfoVO combineInfo) {
        log.info("redeemRatio={}, reduce invest", redeemReq.getRedeemRatio());
        ReduceInvestResp reduceInvestResp = t2ServiceAdapter.reduceInvest(ContextHolder.getSimpleAccount(), redeemReq.getCombineCode(), redeemReq.getRedeemRatio());
        CombRequestRecordEntity combRequestRecord = build(redeemReq, combineInfo.getCombineName(), reduceInvestResp);
        // 记录数据库
        recordAsyncWriter.asyncSaveCombRequestRecord(combRequestRecord);
        if (!reduceInvestResp.isSuccess()) {
            if (NOT_ALLOW_REDUCE_39049.equals(reduceInvestResp.getError_no()) || NOT_ALLOW_REDUCE_39234.equals(reduceInvestResp.getError_no())) {
                throw new BizException(BizErrorCodeEnum.REDUCE_INVEST_ERROR, "存在进行中的委托，完成后方可赎回");
            }
            throw new BizException(BizErrorCodeEnum.REDUCE_INVEST_ERROR);
        }
        RedeemResultVO resultVO = new RedeemResultVO(reduceInvestResp.getComb_request_no(), null, REDEEM_RESULT_TITLE, PURCHASE_RESULT_TIP);
        String currentTime = DateUtils.formatDate(new Date(), DEFAULT_DATE_TIME_FORMAT);
        resultVO.setFlowList(RedeemResultVO.getTradeTimeFlow(currentTime, reduceInvestResp.getPre_affirm_date().toString(), reduceInvestResp.getPre_arrive_date().toString()));
        return resultVO;
    }

    /**
     * 是否全部赎回
     *
     * @param redeemRatio
     * @return
     */
    private boolean isRedeemAll(BigDecimal redeemRatio) {
        return DataUtil.isOne(redeemRatio);
    }

    /**
     * 是否允许解约
     * 当天签约当天限制不能解约，因为客户带资签约之后，如果发起了撤单，然后又发起了解约T+1会导出销户申请给基金公司，
     * 因为基金账户还没有确认，会导致基金公司处理报错
     *
     * @param combineCode
     * @return
     */
    private boolean canCancelAgreement(String combineCode) {
        QueryAgreementResp queryAgreementResp = t2ServiceAdapter.queryAgreement(ContextHolder.getSimpleAccount(), combineCode);
        Optional<AgreementExtDTO> optional = queryAgreementResp.getRows()
                .stream().filter(x -> DateUtil.dateToInt(new Date()) == x.getSign_date().intValue())
                .findAny();
        if (optional.isPresent()) {
            log.warn("sign today, cannot cancel agreement, signDate={}", optional.get().getSign_date());
            return false;
        }
        return true;
    }

    @Override
    public EnableBalanceVO getEnableBalance() {
        // 账户余额
        QueryEnableBalanceResp enableBalance = t2ServiceAdapter.queryEnableBalance(ContextHolder.getSimpleAccount());
        EnableBalanceVO result = new EnableBalanceVO();
        result.setEnableBalance(DataUtil.strToBigDecimal(enableBalance.getEnable_balance()));
        result.setClientId(enableBalance.getClient_id());
        result.setInvestorAccount(enableBalance.getInvestor_account());
        result.setMoneyType(enableBalance.getMoney_type());
        return result;
    }

    @Override
    public TimeCheckResultVO checkTime(String combineCode) {
        log.info("checkTime clientId {} combine code {}", ContextHolder.getSimpleAccount().getClientId(), combineCode);
        TradeTimeCheckRes timeCheckRes = tradeTimeRemoteService.checkTradeTime(ContextHolder.getSimpleAccount(), ContextHolder.getAppInfo().getVersion());
        TimeCheckResultVO timeCheckResultVO = new TimeCheckResultVO();
        // 不支持7*24，直接返回
        if (!timeCheckRes.isSupport_7_24()) {
            timeCheckResultVO.setCanAllTimeTrade(BoolEnum.NO.getId());
            timeCheckResultVO.setIsTradeTime(timeCheckRes.isInTradeTime() ? BoolEnum.YES.getId() : BoolEnum.NO.getId());
            timeCheckResultVO.setTransTip(NON_TRADE_TIME_TOAST);
            return timeCheckResultVO;
        } else {
            timeCheckResultVO.setCanAllTimeTrade(BoolEnum.YES.getId());
            timeCheckResultVO.setIsTradeTime(timeCheckRes.isInTradeTime() ? BoolEnum.YES.getId() : BoolEnum.NO.getId());
        }
        // 支持7*24，判断是否为交易时间
        if (timeCheckRes.isInTradeTime()) {
            return timeCheckResultVO;
        }
        AutoTransBankResp autoTransBankResp = t2ServiceAdapter.getAutoTransBank(ContextHolder.getSimpleAccount());
        if (autoTransBankResp == null || BoolEnum.NO.getId().toString().equals(autoTransBankResp.getAuto_trans())) {
            timeCheckResultVO.setCanAutoTrans(BoolEnum.NO.getId());
            timeCheckResultVO.setTransTip(NO_AUTO_TRANS_TIP);
            return timeCheckResultVO;
        }
        List<BankInfoResp> bankInfoList = t2ServiceAdapter.getClientBankList(ContextHolder.getSimpleAccount());
        timeCheckResultVO.setCanAutoTrans(BoolEnum.YES.getId());
        timeCheckResultVO.setBankInfo(assembleBankInfo(bankInfoList, ContextHolder.getSimpleAccount().getFundAccount()));
        timeCheckResultVO.setAutoTransContractUrl(AUTO_TRANS_CONTRACT_URL);
        timeCheckResultVO.setTransTip(TRANS_TIP);
        timeCheckResultVO.setConfirmDate(getConfirmDate());
        return timeCheckResultVO;
    }

    private String getConfirmDate() {
        String confirmDate = null;
        LocalTime checkTime = DateUtil.intToLocalTime(CHECK_TRADE_TIME);
        Date currentTime = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        if (!tradeCalendarRemoteServiceAdapter.isTradeDate(currentTime) || LocalTime.now().isAfter(checkTime)) {
            confirmDate = formatDate(tradeCalendarRemoteServiceAdapter.queryNextTradeDate(calendar.getTime()));
        } else {
            confirmDate = formatDate(DateUtils.getIntegerDate(currentTime));
        }
        return confirmDate;
    }


    /**
     * 构建赎回记录
     *
     * @param redeemReq
     * @param combineName
     * @param cancelAgreementResp
     * @return
     */
    private CombRequestRecordEntity build(RedeemReq redeemReq, String combineName, CancelAgreementResp cancelAgreementResp) {
        CombRequestRecordEntity entity = new CombRequestRecordEntity();
        BeanUtil.copyProperties(ContextHolder.getSimpleAccount(), entity);
        entity.setOperCode(CombRequestEnum.CANCEL.getCode());
        entity.setOperDesc(CombRequestEnum.CANCEL.getDesc());
        entity.setCombCode(redeemReq.getCombineCode());
        entity.setCombName(combineName);
        entity.setRedeemRatio(redeemReq.getRedeemRatio());
        entity.setResp(JSONObject.toJSONString(cancelAgreementResp));
        entity.setCombRequestNo(cancelAgreementResp.getComb_request_no());
        entity.setOrigCombRequestNo(null);
        entity.setCreateMonth(DateUtil.dateToIntMonth(new Date()));
        entity.setTraceId(MdcUtil.getTraceId());
        entity.setErrorNo(String.valueOf(cancelAgreementResp.getError_no()));
        entity.setErrorInfo(cancelAgreementResp.getError_info());
        return entity;
    }

    /**
     * 构建赎回记录
     *
     * @param revokeReq
     * @param combineName
     * @param recallCombRequestResp
     * @return
     */
    private CombRequestRecordEntity build(RevokeReq revokeReq, String combineName, RecallCombRequestResp recallCombRequestResp) {
        CombRequestRecordEntity entity = new CombRequestRecordEntity();
        BeanUtil.copyProperties(ContextHolder.getSimpleAccount(), entity);
        entity.setOperCode(CombRequestEnum.REVOKE.getCode());
        entity.setOperDesc(CombRequestEnum.REVOKE.getDesc());
        entity.setCombCode(revokeReq.getCombineCode());
        entity.setCombName(combineName);
        entity.setResp(JSONObject.toJSONString(recallCombRequestResp));
        entity.setCombRequestNo(recallCombRequestResp.getComb_request_no());
        entity.setOrigCombRequestNo(revokeReq.getOrigCombRequestNo());
        entity.setCreateMonth(DateUtil.dateToIntMonth(new Date()));
        entity.setTraceId(MdcUtil.getTraceId());
        entity.setErrorNo(String.valueOf(recallCombRequestResp.getError_no()));
        entity.setErrorInfo(recallCombRequestResp.getError_info());
        return entity;
    }

    /**
     * 构建撤单记录
     *
     * @param redeemReq
     * @param combineName
     * @param reduceInvestResp
     * @return
     */
    private CombRequestRecordEntity build(RedeemReq redeemReq, String combineName, ReduceInvestResp reduceInvestResp) {
        CombRequestRecordEntity entity = new CombRequestRecordEntity();
        BeanUtil.copyProperties(ContextHolder.getSimpleAccount(), entity);
        entity.setOperCode(CombRequestEnum.REDUCE_INVEST.getCode());
        entity.setOperDesc(CombRequestEnum.REDUCE_INVEST.getDesc());
        entity.setCombCode(redeemReq.getCombineCode());
        entity.setCombName(combineName);
        entity.setRedeemRatio(redeemReq.getRedeemRatio());
        entity.setResp(JSONObject.toJSONString(reduceInvestResp));
        entity.setCombRequestNo(reduceInvestResp.getComb_request_no());
        entity.setOrigCombRequestNo(null);
        entity.setCreateMonth(DateUtil.dateToIntMonth(new Date()));
        entity.setTraceId(MdcUtil.getTraceId());
        entity.setErrorNo(String.valueOf(reduceInvestResp.getError_no()));
        entity.setErrorInfo(reduceInvestResp.getError_info());
        return entity;
    }

    private String assembleBankInfo(List<BankInfoResp> bankInfoList, String fundAccount) {
        for (BankInfoResp bank : bankInfoList) {
            if (ObjectUtils.nullSafeEquals(bank.getFund_account(), fundAccount)) {
                String bankInfo = "";
                if (!org.springframework.util.StringUtils.isEmpty(bank.getBank_name())) {
                    bankInfo = bank.getBank_name();
                }
                String bankAccount = bank.getBank_account();
                if (!StringUtils.isEmpty(bankAccount) && bankAccount.length() > 4) {
                    //  展示后四位银行卡号
                    bankInfo = bankInfo + "(" + bankAccount.substring(bankAccount.length() - 4) + ")";
                }
                return bankInfo;
            }
        }
        return null;
    }
}
