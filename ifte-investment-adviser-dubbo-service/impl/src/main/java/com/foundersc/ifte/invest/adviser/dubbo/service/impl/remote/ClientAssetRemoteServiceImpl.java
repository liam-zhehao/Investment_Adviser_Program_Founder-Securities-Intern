package com.foundersc.ifte.invest.adviser.dubbo.service.impl.remote;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombBusTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombEntrustStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.TradeTimeCheckRes;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombinePosition;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.*;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.ClientAssetRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombineRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.TradeTimeRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.async.TraceableCallable;
import com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants;
import com.foundersc.ifte.invest.adviser.dubbo.entity.InvestorAccount;
import com.foundersc.ifte.invest.adviser.dubbo.enums.PeriodOperationEnum;
import com.foundersc.ifte.invest.adviser.dubbo.service.ClientAssetQueryService;
import com.foundersc.ifte.invest.adviser.dubbo.service.ClientEntrustQueryService;
import com.foundersc.ifte.invest.adviser.dubbo.service.InvestorAccountService;
import com.foundersc.ifte.invest.adviser.dubbo.service.impl.support.TradeCalendarRemoteServiceAdapter;
import com.foundersc.ifte.invest.adviser.dubbo.util.DateUtil;
import com.foundersc.ifte.invest.adviser.dubbo.util.ThreadPoolUtil;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.QUERY_COMB_ASSET_ERROR;
import static com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants.DEFAULT_END_TIME;

@Service("clientAssetRemoteService")
@Slf4j
public class ClientAssetRemoteServiceImpl implements ClientAssetRemoteService {

    private static final ThreadPoolExecutor EXECUTOR = ThreadPoolUtil.getInstance();

    private static final ListeningExecutorService LISTENING_EXECUTOR = ThreadPoolUtil.getListenExecutor();

    @Autowired
    private ClientAssetQueryService clientAssetQueryService;

    @Autowired
    private ClientEntrustQueryService clientEntrustQueryService;

    @Autowired
    private InvestorAccountService investorAccountService;

    private final static String TRADE_TIP = "交易日 9:30-15:00 开放转入转出";

    private final static Integer LATCH_NUM = 2;

    @Autowired
    private CombineRemoteService combineRemoteService;

    @Autowired
    private TradeCalendarRemoteServiceAdapter tradeCalendarRemoteServiceAdapter;

    @Autowired
    private TradeTimeRemoteService tradeTimeRemoteService;

    @Override
    public CombAssetInfo getClientCombAssetInfo(SimpleAccount simpleAccount, boolean isTotalAssetOnly) {
        CombAssetInfo combAssetInfo = new CombAssetInfo();
        try {
            // 1.查询是否存在投顾账户
            List<InvestorAccount> investorAccounts = investorAccountService.queryClientInvestorAccount(simpleAccount);
            if (CollectionUtils.isEmpty(investorAccounts)) {
                // log.info("[client investAccount] clientId {} does not have investor account", simpleAccount.getClientId());
                return combAssetInfo;
            }
            simpleAccount.setInvestorAccount(simpleAccount.getFundAccount());
            combAssetInfo.setHasInvestorAccount(true);
            // 只查询总资产
            if (isTotalAssetOnly) {
                assembleTotalAsset(simpleAccount, combAssetInfo, Boolean.TRUE);
                return combAssetInfo;
            }
            Future<ClientTotalAsset> totalAssetFuture = asyncQueryClientTotalAsset(simpleAccount);
            Future<List<ClientCombAsset>> combAssetFuture = asyncQueryCombAssets(simpleAccount);
            ClientTotalAsset clientTotalAsset = getFromFuture(totalAssetFuture);
            List<ClientCombAsset> clientCombAssets = getFromFuture(combAssetFuture);
            List<CombineEntrust> combineEntrusts = getCombineEntrustsInTransit(simpleAccount);
            Set<String> combCodes = combineEntrusts.stream().map(CombineEntrust::getCombineCode).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(clientCombAssets)) {
                // 组合资产不为0，或存在在途委托（比如组合解约）
                clientCombAssets = clientCombAssets.stream().filter(asset -> !asset.getCombineAsset().equals(0.0)
                        || combCodes.contains(asset.getCombineCode())).collect(Collectors.toList());
            }
            if (clientTotalAsset != null) {
                combAssetInfo.setHasCombAsset(true);
                combAssetInfo.setClientTotalAsset(clientTotalAsset);
            }
            combAssetInfo.setClientCombAssets(clientCombAssets);
            combAssetInfo.setCombineEntrusts(combineEntrusts);
            log.info("[client asset] client {} getClientCombAssetInfo result success.", simpleAccount.getClientId());
        } catch (Exception e) {
            log.error("[client asset] clientId {} query position error.", simpleAccount.getClientId(), e);
        }

        return combAssetInfo;
    }


    /**
     * 异步查询组合资产
     *
     * @param simpleAccount
     * @return
     */
    public Future<List<ClientCombAsset>> asyncQueryCombAssets(final SimpleAccount simpleAccount) {
        return EXECUTOR.submit(new QueryCombAssetsTask(simpleAccount, clientAssetQueryService));
    }

    /**
     * 查询组合资产任务
     */
    class QueryCombAssetsTask extends TraceableCallable<List<ClientCombAsset>> {
        /**
         * 客户信息
         */
        private SimpleAccount simpleAccount;
        /**
         * 客户组合资产查询服务
         */
        private ClientAssetQueryService clientAssetQueryService;

        public QueryCombAssetsTask(SimpleAccount simpleAccount, ClientAssetQueryService clientAssetQueryService) {
            this.simpleAccount = simpleAccount;
            this.clientAssetQueryService = clientAssetQueryService;
        }

        @Override
        protected List<ClientCombAsset> doCall() throws Exception {
            try {
                List<ClientCombAsset> clientCombAssets = clientAssetQueryService.getClientCombAsset(simpleAccount, null);
                if (CollectionUtils.isEmpty(clientCombAssets)) {
                    log.info("addCombAssetsTask clientId {} does not have comb asset", simpleAccount.getClientId());
                }
                long t1 = System.currentTimeMillis();
                // 查询组合信息
                List<ListenableFuture<Boolean>> futureList = new ArrayList<>();
                for (ClientCombAsset clientCombAsset : clientCombAssets) {
                    futureList.add(LISTENING_EXECUTOR.submit(new PostProcessTargetCombAssetTask(simpleAccount, clientCombAsset)));
                }
                ListenableFuture<List<Boolean>> listListenableFuture = Futures.allAsList(futureList);
                listListenableFuture.get(3, TimeUnit.SECONDS);
                log.info("fill clientCombAsset comb info cost {}ms", System.currentTimeMillis() - t1);
                return clientCombAssets;
            } catch (Exception e) {
                log.error("addCombAssetsTask error, account: " + simpleAccount, e);
            }
            return null;
        }
    }

    /**
     * 填充组合资产相关信息
     */
    class PostProcessTargetCombAssetTask extends TraceableCallable<Boolean> {
        /**
         * 客户信息
         */
        private SimpleAccount simpleAccount;
        /**
         * 客户组合资产信息
         */
        private ClientCombAsset clientCombAsset;

        PostProcessTargetCombAssetTask(SimpleAccount simpleAccount, ClientCombAsset clientCombAsset) {
            this.simpleAccount = simpleAccount;
            this.clientCombAsset = clientCombAsset;
        }

        @Override
        protected Boolean doCall() throws Exception {
            return postProcessTargetCombAsset(simpleAccount, clientCombAsset);
        }
    }

    /**
     * 对目标盈组合资产进行后置处理
     *
     * @param simpleAccount
     * @param clientCombAsset
     */
    private boolean postProcessTargetCombAsset(SimpleAccount simpleAccount, ClientCombAsset clientCombAsset) {
        String combineCode = clientCombAsset.getCombineCode();
        try {
            CombineInfo combInfo = combineRemoteService.getCombInfo(simpleAccount, combineCode);
            clientCombAsset.setTargetCombFullName(combInfo.getTargetCombFullName());
            clientCombAsset.setTargetComb(combInfo.isTargetComb());
            clientCombAsset.setCombRiskLevel(combInfo.getCombRiskLevel());
            clientCombAsset.setBuyStartDate(combInfo.getBuyStartDate());
            clientCombAsset.setBuyStartTime(combInfo.getBuyStartTime());
            clientCombAsset.setBuyEndDate(combInfo.getBuyEndDate());
            clientCombAsset.setBuyEndTime(combInfo.getBuyEndTime());
            clientCombAsset.setProfitValidDate(combInfo.getProfitValidDate());
            clientCombAsset.setOperationalStartDate(combInfo.getOperationalStartDate());
            clientCombAsset.setOperationalStartTime(combInfo.getOperationalStartTime());
            clientCombAsset.setCombEndDate(combInfo.getCombEndDate());
            clientCombAsset.setCombEndTime(combInfo.getCombEndTime());
            clientCombAsset.setStopProfitDate(combInfo.getStopProfitDate());
            clientCombAsset.setStopProfitTime(combInfo.getStopProfitTime());
            clientCombAsset.setDealStatus(combInfo.getDealStatus());
            return true;
        } catch (Exception e) {
            log.error("get CombInfo fail, combineCode={}", combineCode, e);
            // 出现异常时，默认非目标盈
            clientCombAsset.setTargetComb(false);
            return false;
        }
    }

    /**
     * 组合持仓饼状图
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    private CombinePosition getCombinePosition(SimpleAccount simpleAccount, String combineCode) {
        CombinePosition combinePosition = null;
        try {
            combinePosition = combineRemoteService.getCombinePosition(simpleAccount, combineCode);
        } catch (Exception e) {
            // 出现异常时
            log.error("getCombinePosition fail, combineCode={}", combineCode);
        }
        return combinePosition;
    }

    /**
     * 异步查询客户组合总资产
     *
     * @param simpleAccount
     * @return
     */
    public Future<ClientTotalAsset> asyncQueryClientTotalAsset(final SimpleAccount simpleAccount) {
        return EXECUTOR.submit(new QueryClientTotalAsset(simpleAccount, clientAssetQueryService));
    }

    /**
     * 查询客户组合总资产任务
     */
    class QueryClientTotalAsset extends TraceableCallable<ClientTotalAsset> {
        /**
         * 客户信息
         */
        private SimpleAccount simpleAccount;

        /**
         * 查询客户资产服务
         */
        private ClientAssetQueryService clientAssetQueryService;

        public QueryClientTotalAsset(SimpleAccount simpleAccount, ClientAssetQueryService clientAssetQueryService) {
            this.simpleAccount = simpleAccount;
            this.clientAssetQueryService = clientAssetQueryService;
        }

        @Override
        protected ClientTotalAsset doCall() throws Exception {
            try {
                ClientTotalAsset clientTotalAsset = clientAssetQueryService.getClientTotalAsset(simpleAccount);
                if (clientTotalAsset == null) {
                    log.info("addClientTotalAssetTask clientId {} does not have total asset", simpleAccount.getClientId());
                    return null;
                }
                return clientTotalAsset;
            } catch (Exception e) {
                log.error("addClientTotalAssetTask error, account: " + simpleAccount, e);
            }
            return null;
        }
    }

    private <T> T getFromFuture(Future<T> future) {
        if (future == null) {
            return null;
        }
        try {
            return future.get(1L, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 查询在途委托
     *
     * @param simpleAccount
     * @return
     */
    private List<CombineEntrust> getCombineEntrustsInTransit(SimpleAccount simpleAccount) {
        CombineEntrustReq combineEntrustReq = new CombineEntrustReq();
        combineEntrustReq.setBusinessType(StrUtil.join(CommonConstants.SPLIT_COMMA, CombBusTypeEnum.tradeRecordBusTypes));
        List<CombineEntrust> combineEntrusts = clientEntrustQueryService.queryCurrEntrusts(simpleAccount, combineEntrustReq);
        if (!CollectionUtils.isEmpty(combineEntrusts)) {
            // 过滤掉【已撤单】和【失败状态】的委托
            combineEntrusts = combineEntrusts.stream().filter(entrust ->
                    !CombEntrustStatusEnum.ORDER_WITHDRAW.getCode().equals(entrust.getCombRequestStatus()))
                    .filter(entrust -> !CombEntrustStatusEnum.failedStatuses.contains(entrust.getCombRequestStatus())).collect(Collectors.toList());
            return combineEntrusts;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 总资产查询封装
     *
     * @param simpleAccount
     * @param combAssetInfo
     * @param isTotalAssetOnly
     */
    private void assembleTotalAsset(SimpleAccount simpleAccount, CombAssetInfo combAssetInfo, Boolean isTotalAssetOnly) {
        ClientTotalAsset clientTotalAsset = clientAssetQueryService.getClientTotalAsset(simpleAccount);
        if (clientTotalAsset == null) {
            log.info("[client asset] clientId {} does not have comb asset", simpleAccount.getClientId());
            return;
        }
        // 总资产包括在途的
        combAssetInfo.setTotalCombAsset(clientTotalAsset.getCombAsset());
        combAssetInfo.setHasCombAsset(true);
        if (!isTotalAssetOnly) {
            combAssetInfo.setClientTotalAsset(clientTotalAsset);
        }
    }

    @Override
    public CombPositionDetail getClientCombPositionDetail(SimpleAccount simpleAccount, String combineCode) {
        return getClientCombPositionDetail(null, simpleAccount, combineCode);
    }

    @Override
    public CombPositionDetail getClientCombPositionDetail(String appVersion, SimpleAccount simpleAccount, String combineCode) {
        if (StringUtils.isEmpty(combineCode)) {
            return null;
        }
        CombPositionDetail combPositionDetail = new CombPositionDetail();
        log.info("combPositionDetail isBuy={}, isRedeem={}", combPositionDetail.isCanBuy(), combPositionDetail.isCanRedeem());
        try {
            // 1.查询组合持仓信息
            log.info("[comb position] clientId {} query comb position {} begin.", simpleAccount.getClientId(), combineCode);
            List<ClientCombAsset> combAssets = clientAssetQueryService.getClientCombAsset(simpleAccount, combineCode);
            if (!CollectionUtils.isEmpty(combAssets)) {
                combPositionDetail.setClientCombAsset(combAssets.get(0));
            }
            postProcessTargetCombAsset(simpleAccount, combPositionDetail.getClientCombAsset());
            boolean isTargetComb = Boolean.TRUE.equals(combPositionDetail.getClientCombAsset().getTargetComb());
            // 2.查询组合成分产品 如果首次签约，在途状态下该接口返回数据为空
            List<CombShareItem> shareItems = clientAssetQueryService.getCombPositionShares(simpleAccount, combineCode);
            combPositionDetail.setShareItems(shareItems);

            combPositionDetail.setTradeTip(TRADE_TIP);
            if (isTargetComb) {
                log.info("is TargetComb is true");
                // 目标盈组合持仓详情展示组合成分饼状图
                CombinePosition combinePosition = getCombinePosition(simpleAccount, combPositionDetail.getClientCombAsset().getCombineCode());
                combPositionDetail.setTypeRatios(combinePosition == null ? new ArrayList<>() : combinePosition.getTypeRatios());
                combPositionDetail.setPositionTypeRatioUpdateDate(combinePosition == null ? null : combinePosition.getUpdateDate());
                CombPositionDetail.TargetCombOperationPeriod operationPeriod = getTargetCombOperationPeriod(combPositionDetail.getClientCombAsset());
                combPositionDetail.setTargetCombOperationPeriod(operationPeriod);
                // 参与期可以购买
                combPositionDetail.setCanBuy(operationPeriod.isInParticipationPeriod());
                if (!combPositionDetail.isCanBuy()) {
                    combPositionDetail.setCannotBuyTip("只参与期可转入资金");
                }
                // 是否支持转出
                FlagAndTip redeemFlagAndTip = canTargetCombRedeem(simpleAccount, combPositionDetail.getClientCombAsset(), operationPeriod);
                combPositionDetail.setCanRedeem(redeemFlagAndTip.isFlag());
                combPositionDetail.setCannotRedeemTip(redeemFlagAndTip.getTip());
                combPositionDetail.setTradeTip(org.apache.commons.lang3.StringUtils.EMPTY);
            }
            // 是否有可转出份额
            CombFetchAsset combFetchAsset = clientAssetQueryService.getCombFetchAsset(simpleAccount, combineCode);
            if (combFetchAsset.getMaxFetchRatio().compareTo(BigDecimal.ZERO) <= 0
                    && combFetchAsset.getAllRedeemRatio().compareTo(BigDecimal.ZERO) <= 0) {
                combPositionDetail.setCanRedeem(false);
                combPositionDetail.setCannotRedeemTip("当前无可转出份额");
            }
            // 7*24判断
            TradeTimeCheckRes tradeTimeCheckRes = tradeTimeRemoteService.checkTradeTime(appVersion);
            if (!tradeTimeCheckRes.isInTradeTime()) {
                if (!tradeTimeCheckRes.isSupport_7_24()) {
                    // 如果转入不支持7*24
                    combPositionDetail.setCanBuy(false);
                    combPositionDetail.setCannotBuyTip(tradeTimeCheckRes.getTip());
                }
                // 转出不支持7*24
                combPositionDetail.setCanRedeem(false);
                combPositionDetail.setCannotRedeemTip(tradeTimeCheckRes.getTip());
            }
            log.info("isTargetComb={}, isCanBuy={}, isCanRedeem={}", isTargetComb, combPositionDetail.isCanBuy(), combPositionDetail.isCanRedeem());
        } catch (Exception e) {
            log.error(QUERY_COMB_ASSET_ERROR + " [comb position] clientId {} query comb position {} error", simpleAccount.getClientId(), combineCode, e);
            return null;
        }

        log.info("[comb position] clientId {} query comb position {} end.", simpleAccount.getClientId(), combineCode);
        return combPositionDetail;
    }

    /**
     * 目标盈是否可以转出
     *
     * @param simpleAccount
     * @param clientCombAsset
     * @param operationPeriod
     * @return
     */
    private FlagAndTip canTargetCombRedeem(SimpleAccount simpleAccount, ClientCombAsset clientCombAsset, CombPositionDetail.TargetCombOperationPeriod operationPeriod) {
        if (clientCombAsset.getProfitValidDate() != null && clientCombAsset.getProfitValidDate() > 0) {
            // 已止盈，则不可以转出
            return new FlagAndTip(false, "策略已止盈，系统将会自动发起转出，无需手动操作");
        }
        if (clientCombAsset.getCombEndDate() != null && DateUtil.dateToInt(new Date()) == clientCombAsset.getCombEndDate()) {
            // 今日为策略到期日
            return new FlagAndTip(false, "策略已到期，系统将会自动发起转出，无需手动操作");
        }
        if (clientCombAsset.getCombEndDate() != null && DateUtil.isAfter(clientCombAsset.getCombEndDate(), clientCombAsset.getCombEndTime())) {
            // 已到期，则不可以转出
            return new FlagAndTip(false, "策略已到期，系统将会自动发起转出，无需手动操作");
        }
        // 组合调仓中无法转出
        if (isAdjusting(simpleAccount, clientCombAsset.getCombineCode())) {
            return new FlagAndTip(false, "组合调仓中，暂时无法转出，可结束后再发起");
        }
        if (operationPeriod.isInObservationPeriod() || operationPeriod.isInStopProfitPeriod()) {
            // 观察期或止盈期可以转出
            return new FlagAndTip(true, org.apache.commons.lang3.StringUtils.EMPTY);
        }
        return new FlagAndTip(false, "观察期或止盈期才支持转出");
    }

    /**
     * 组合是否调仓中
     *
     * @param simpleAccount
     * @param combineCode
     * @return
     */
    private boolean isAdjusting(SimpleAccount simpleAccount, String combineCode) {
        CombineEntrustReq entrustReq = new CombineEntrustReq();
        entrustReq.setCombineCode(combineCode);
        String businessType = CombBusTypeEnum.COMB_ADJUEST.getCode();
        entrustReq.setBusinessType(businessType);
        List<CombineEntrust> combineEntrusts = clientEntrustQueryService.queryCurrEntrusts(simpleAccount, entrustReq);
        if (CollectionUtils.isEmpty(combineEntrusts)) {
            return false;
        }
        // 如果存在组合申请状态不为CombEntrustStatusEnum.AFFIRM_SUCCESS且不为CombEntrustStatusEnum.AFFIRM_FAIL的申请，则表示在调仓中
        Optional<CombineEntrust> optional = combineEntrusts.stream().filter(x -> !CombEntrustStatusEnum.AFFIRM_SUCCESS.getCode().equals(x.getCombRequestStatus())
                && !CombEntrustStatusEnum.AFFIRM_FAIL.getCode().equals(x.getCombRequestStatus())).findAny();
        return optional.isPresent();
    }

    /**
     * 标志和提示
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class FlagAndTip {
        /**
         * 标志
         */
        private boolean flag;
        /**
         * 提示
         */
        private String tip;
    }

    /**
     * 获取运作流程
     *
     * @return
     */
    private CombPositionDetail.TargetCombOperationPeriod getTargetCombOperationPeriod(ClientCombAsset clientCombAsset) {
        CombPositionDetail.TargetCombOperationPeriod result = new CombPositionDetail.TargetCombOperationPeriod();
        result.setCombEndDateDesc(DateUtil.intToFormatDate(clientCombAsset.getCombEndDate()));
        result.setInitDate(DateUtil.dateToInt(new Date()));
        result.setProfitValidDate(clientCombAsset.getProfitValidDate());
        result.setPeriods(getOperationPeriods(clientCombAsset));
        List<CombPositionDetail.Period> periods = new ArrayList<>();
        // 结束时间，使用默认结束时间
        CombPositionDetail.Period participationPeriod = calculatePeriod("参与期", clientCombAsset.getBuyStartDate(), clientCombAsset.getBuyStartTime(),
                clientCombAsset.getBuyEndDate(), DEFAULT_END_TIME, PeriodOperationEnum.getParticipationOperations());
        result.setInParticipationPeriod(participationPeriod.isInThisPeriod());
        CombPositionDetail.Period observationPeriod = calculatePeriod("观察期", clientCombAsset.getOperationalStartDate(), clientCombAsset.getOperationalStartTime(),
                clientCombAsset.getStopProfitDate(), DEFAULT_END_TIME, PeriodOperationEnum.getObservationOperations());
        result.setInObservationPeriod(observationPeriod.isInThisPeriod());
        // 止盈期开始时间为stopProfitDate的下一个自然日
        CombPositionDetail.Period stopProfitPeriod = calculatePeriod("止盈期", getNextDate(clientCombAsset.getStopProfitDate()), 0,
                clientCombAsset.getCombEndDate(), DEFAULT_END_TIME, PeriodOperationEnum.getProfitOperations());
        result.setInStopProfitPeriod(stopProfitPeriod.isInThisPeriod());
        periods.add(participationPeriod);
        periods.add(observationPeriod);
        periods.add(stopProfitPeriod);
        return result;
    }

    /**
     * 获取下一个自然日
     *
     * @param intDate
     * @return
     */
    private Integer getNextDate(Integer intDate) {
        LocalDate localDate = DateUtil.intToLocalDate(intDate);
        LocalDate nextLocalDate = localDate.plusDays(1);
        return DateUtil.dateToInt(DateUtil.localDateToDate(nextLocalDate));
    }

    /**
     * 获取intDate的下一个交易日（不包括intDate）
     *
     * @param intDate
     * @return
     */
    private Integer getNextTradeDate(Integer intDate) {
        LocalDate localDate = DateUtil.intToLocalDate(intDate);
        LocalDate nextLocalDate = localDate.plusDays(1);
        return tradeCalendarRemoteServiceAdapter.queryNextTradeDate(DateUtil.localDateToDate(nextLocalDate));
    }

    /**
     * 运作阶段
     *
     * @param clientCombAsset
     * @return
     */
    private List<CombPositionDetail.Period> getOperationPeriods(ClientCombAsset clientCombAsset) {
        List<CombPositionDetail.Period> result = new ArrayList<>();
        result.add(calculatePeriod("参与期", clientCombAsset.getBuyStartDate(), clientCombAsset.getBuyStartTime(),
                clientCombAsset.getBuyEndDate(), DEFAULT_END_TIME, PeriodOperationEnum.getParticipationOperations()));
        result.add(calculatePeriod("观察期", clientCombAsset.getOperationalStartDate(), clientCombAsset.getOperationalStartTime(),
                clientCombAsset.getStopProfitDate(), DEFAULT_END_TIME, PeriodOperationEnum.getObservationOperations()));
        // 止盈期的开始时间取stopProfitDate的下一个自然日
        result.add(calculatePeriod("止盈期", getNextDate(clientCombAsset.getStopProfitDate()), 0,
                clientCombAsset.getCombEndDate(), DEFAULT_END_TIME, PeriodOperationEnum.getProfitOperations()));
        return result;
    }

    /**
     * 计算Period
     *
     * @param name
     * @param startDate
     * @param startTime
     * @param endDate
     * @param endTime
     * @param operations
     * @return
     */
    public static CombPositionDetail.Period calculatePeriod(String name, Integer startDate, Integer startTime, Integer endDate, Integer endTime, List<CombPositionDetail.Operation> operations) {
        CombPositionDetail.Period period = new CombPositionDetail.Period();
        period.setName(name);
        period.setStartDate(startDate);
        period.setStartTime(startTime);
        period.setStartDateFormat(DateUtil.intToFormatDate(startDate));
        period.setEndDate(endDate);
        period.setEndTime(endTime);
        period.setOperations(operations);
        if (DateUtil.isBefore(startDate, startTime)) {
            // 当前日期小于起始日期
            period.setPercent(BigDecimal.ZERO);
            period.setInThisPeriod(false);
        } else if (DateUtil.isAfter(endDate, endTime)) {
            // 当前日期大于结束日期
            period.setPercent(BigDecimal.ONE);
            period.setInThisPeriod(false);
        } else {
            period.setInThisPeriod(true);
            LocalDate startLocalDate = DateUtil.intToLocalDate(startDate);
            LocalDate endLocalDate = DateUtil.intToLocalDate(endDate);
            LocalDate nowLocalDate = DateUtil.localDate();
            // 当前日期介于其实日期和结束日期
            long intervalDays = startLocalDate.until(endLocalDate, ChronoUnit.DAYS) + 1;
            long pastDays = startLocalDate.until(nowLocalDate, ChronoUnit.DAYS) + 1;
            if (pastDays >= intervalDays) {
                period.setPercent(BigDecimal.ONE);
            } else {
                period.setPercent(new BigDecimal(pastDays).divide(new BigDecimal(intervalDays), 2, RoundingMode.HALF_UP));
            }
        }
        return period;
    }

    @Override
    public ClientCombAsset getSingleCombAsset(SimpleAccount simpleAccount, String combineCode) {
        if (StringUtils.isEmpty(combineCode)) {
            return null;
        }
        log.info("[comb position] clientId {} query single comb {} begin.", simpleAccount.getClientId(), combineCode);
        try {
            List<ClientCombAsset> combAssets = clientAssetQueryService.getClientCombAsset(simpleAccount, combineCode);
            if (!CollectionUtils.isEmpty(combAssets)) {
                ClientCombAsset clientCombAsset = combAssets.get(0);
                postProcessTargetCombAsset(simpleAccount, clientCombAsset);
                return clientCombAsset;
            }
        } catch (Exception e) {
            log.error(QUERY_COMB_ASSET_ERROR + " [comb position] clientId {} query single comb {} error", simpleAccount.getClientId(), combineCode, e);
        }
        return null;
    }

    @Override
    public CombFetchAsset getClientCombFetchAsset(SimpleAccount simpleAccount, String combineCode) {
        if (StringUtils.isEmpty(combineCode)) {
            return null;
        }
        CombFetchAsset combFetchAsset = clientAssetQueryService.getCombFetchAsset(simpleAccount, combineCode);
        return combFetchAsset;
    }
}
