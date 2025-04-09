package com.foundersc.ifte.invest.adviser.dubbo.service.impl;

import com.foundersc.ifc.portfolio.t2.request.CombEntrustReq;
import com.foundersc.ifc.portfolio.t2.response.invest.CombineEntrustDTO;
import com.foundersc.ifc.portfolio.t2.response.invest.CombineEntrustResp;
import com.foundersc.ifc.portfolio.t2.service.ClientAssetService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombEntrustStatusEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombineInfo;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrust;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombineEntrustReq;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.CombineRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.async.TraceableCallable;
import com.foundersc.ifte.invest.adviser.dubbo.service.ClientEntrustQueryService;
import com.foundersc.ifte.invest.adviser.dubbo.util.ObjectCopyUtil;
import com.foundersc.ifte.invest.adviser.dubbo.util.ThreadPoolUtil;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.*;

@Service
@Slf4j
public class ClientEntrustQueryServiceImpl implements ClientEntrustQueryService {
    @Autowired
    private ClientAssetService clientAssetService;

    @Autowired
    private CombineRemoteService combineRemoteService;

    private static final ListeningExecutorService LISTENING_EXECUTOR = ThreadPoolUtil.getListenExecutor();

    @Override
    public List<CombineEntrust> queryCurrEntrusts(SimpleAccount simpleAccount, CombineEntrustReq entrustReq) {
        CombEntrustReq combEntrustReq = new CombEntrustReq();
        if (entrustReq != null) {
            combEntrustReq.setCombineCode(entrustReq.getCombineCode());
            combEntrustReq.setCombRequestNo(entrustReq.getCombRequestNo());
            combEntrustReq.setEnCombBusinessType(entrustReq.getBusinessType());
            combEntrustReq.setBeginDate(entrustReq.getBeginDate());
            combEntrustReq.setEndDate(entrustReq.getEndDate());
        }
        BaseResult<CombineEntrustResp> combEntrustResult = clientAssetService.getClientCombEntrust(simpleAccount, combEntrustReq);
        if (!combEntrustResult.isSuccess()) {
            log.error(QUERY_CURR_ENTRUST_ERROR + "[query entrust] clientId {} query entrust error {}", simpleAccount.getClientId(), combEntrustResult.getErrorMsg());
            throw new BusinessException(QUERY_CURR_ENTRUST_ERROR);
        }
        if (combEntrustResult.getData() == null || CollectionUtils.isEmpty(combEntrustResult.getData().getRows())) {
            log.info("[query entrust] clientId {} curr entrust size is zero.", simpleAccount.getClientId());
            return new ArrayList<>();
        }
        List<CombineEntrustDTO> combineEntrustDTOs = combEntrustResult.getData().getRows();
        if (CollectionUtils.isEmpty(combineEntrustDTOs)) {
            return new ArrayList<>();
        }
        List<CombineEntrust> combineEntrusts = new ArrayList<>(combineEntrustDTOs.size());
        ObjectCopyUtil.copyCombineEntrusts(combineEntrustDTOs, combineEntrusts);
        if (entrustReq.isFillCombFullInfo()) {
            updateCombEntrusts(simpleAccount, combineEntrusts);
        }
        return combineEntrusts;
    }

    /**
     * 更新组合申请信息
     *
     * @param simpleAccount
     * @param combineEntrusts
     */
    private void updateCombEntrusts(SimpleAccount simpleAccount, List<CombineEntrust> combineEntrusts) {
        // 查询组合信息
        List<ListenableFuture<Boolean>> futureList = new ArrayList<>();
        for (CombineEntrust combineEntrust : combineEntrusts) {
            futureList.add(LISTENING_EXECUTOR.submit(new PostProcessTargetCombEntrustTask(simpleAccount, combineEntrust)));
        }
        ListenableFuture<List<Boolean>> listListenableFuture = Futures.allAsList(futureList);
        try {
            listListenableFuture.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("update combineEntrust fail", e);
        }
    }

    /**
     * 填充组合资产相关信息
     */
    class PostProcessTargetCombEntrustTask extends TraceableCallable<Boolean> {
        /**
         * 客户信息
         */
        private SimpleAccount simpleAccount;
        /**
         * 组合委托
         */
        private CombineEntrust combineEntrust;

        PostProcessTargetCombEntrustTask(SimpleAccount simpleAccount, CombineEntrust combineEntrust) {
            this.simpleAccount = simpleAccount;
            this.combineEntrust = combineEntrust;
        }

        @Override
        protected Boolean doCall() throws Exception {
            return postProcessTargetCombAsset(simpleAccount, combineEntrust);
        }
    }

    /**
     * 对目标盈组合资产进行后置处理
     *
     * @param simpleAccount
     * @param combineEntrust
     */
    private boolean postProcessTargetCombAsset(SimpleAccount simpleAccount, CombineEntrust combineEntrust) {
        String combineCode = combineEntrust.getCombineCode();
        try {
            CombineInfo combInfo = combineRemoteService.getCombInfo(simpleAccount, combineCode);
            combineEntrust.setTargetCombFullName(combInfo.getTargetCombFullName());
            combineEntrust.setTargetComb(combInfo.isTargetComb());
            return true;
        } catch (Exception e) {
            log.error("get CombInfo fail, combineCode={}", combineCode, e);
            // 出现异常时，默认非目标盈
            combineEntrust.setTargetComb(false);
            return false;
        }
    }

    @Override
    public List<CombineEntrust> queryHisEntrusts(SimpleAccount simpleAccount, CombineEntrustReq entrustReq) {
        CombEntrustReq combEntrustReq = new CombEntrustReq();
        if (entrustReq != null) {
            combEntrustReq.setCombineCode(entrustReq.getCombineCode());
            combEntrustReq.setEnCombBusinessType(entrustReq.getBusinessType());
            combEntrustReq.setBeginDate(entrustReq.getBeginDate());
            combEntrustReq.setEndDate(entrustReq.getEndDate());
        }
        List<CombineEntrustDTO> combineEntrustDTOs = new ArrayList<>();
        List<CombineEntrust> combineEntrusts = new ArrayList<>();
        BaseResult<CombineEntrustResp> combineEntrustResult = null;
        try {
            combineEntrustResult = clientAssetService.getClientCombHisEntrust(simpleAccount, combEntrustReq);
            if (!combineEntrustResult.isSuccess()) {
                if (combineEntrustResult.getData() != null) {
                    log.error("[his entrust] clientId {} query his entrust error {}", simpleAccount.getClientId(), combineEntrustResult.getData().getError_info());
                } else {
                    log.error("[his entrust] clientId {} query his entrust error {}", simpleAccount.getClientId(), combineEntrustResult.getErrorMsg());
                }
                return combineEntrusts;
            }
            CombineEntrustResp entrustResp = combineEntrustResult.getData();
            if (entrustResp == null) {
                log.info("[his entrust] clientId {} his entrust size is 0", simpleAccount.getClientId());
                return combineEntrusts;
            }

            if (!CollectionUtils.isEmpty(entrustResp.getRows())) {
                combineEntrustDTOs = entrustResp.getRows();
            }
        } catch (Exception e) {
            log.error("[his entrust] clientId {} query exception", simpleAccount.getClientId(), e);
        }
        if (!CollectionUtils.isEmpty(combineEntrustDTOs)) {
            ObjectCopyUtil.copyCombineEntrusts(combineEntrustDTOs, combineEntrusts);
        }
        if (entrustReq.isFillCombFullInfo()) {
            updateCombEntrusts(simpleAccount, combineEntrusts);
        }
        log.info("[his entrust] clientId {} get his entrust size {}", simpleAccount.getClientId(), combineEntrusts.size());
        return combineEntrusts;
    }

    @Override
    public CombineEntrust queryEntrustByRequestNo(SimpleAccount simpleAccount, String combRequestNo, Integer requestDate) {
        Preconditions.checkArgument(!StringUtils.isEmpty(combRequestNo), PARAM_NULL.getDesc());
        log.info("[query single entrust] clientId {} combRequestNo {}", simpleAccount.getClientId(), combRequestNo);
        CombEntrustReq combEntrustReq = new CombEntrustReq();
        combEntrustReq.setCombRequestNo(combRequestNo);
        // 首先查询组合申请
        BaseResult<CombineEntrustResp> combEntrustResult = clientAssetService.getClientCombEntrust(simpleAccount, combEntrustReq);
        CombineEntrust combineEntrust = new CombineEntrust();
        if (checkResult(combEntrustResult)) {
            CombineEntrustDTO combineEntrustDTO = combEntrustResult.getData().getRows().get(0);
            ObjectCopyUtil.copyCombineEntrust(combineEntrustDTO, combineEntrust);
            combineEntrust.setIsCurrent(true);
            log.info("[query single entrust] clientId {} combRequestNo {} is running", simpleAccount.getClientId(), combRequestNo);
            updateCombEntrusts(simpleAccount, Arrays.asList(combineEntrust));
            return combineEntrust;
        }

        if (!combEntrustResult.isSuccess()) {
            log.error("[query single entrust] query curr combRequestNo {} error {}", combRequestNo, combEntrustResult.getErrorMsg());
        }
        // 组合申请中没有，则查询组合历史申请
        combEntrustReq.setBeginDate(requestDate);
        combEntrustReq.setEndDate(requestDate);
        combEntrustResult = clientAssetService.getClientCombHisEntrust(simpleAccount, combEntrustReq);

        if (checkResult(combEntrustResult)) {
            CombineEntrustDTO combineEntrustDTO = combEntrustResult.getData().getRows().get(0);
            ObjectCopyUtil.copyCombineEntrust(combineEntrustDTO, combineEntrust);
            combineEntrust.setIsCurrent(false);
            log.info("[query single entrust] clientId {} combRequestNo {} is in history", simpleAccount.getClientId(), combRequestNo);
            updateCombEntrusts(simpleAccount, Arrays.asList(combineEntrust));
            return combineEntrust;
        }

        if (!combEntrustResult.isSuccess()) {
            log.error(COMB_REQUEST_NO_NULL + "[query single entrust] query his combRequestNo {} error {}", combRequestNo, combEntrustResult.getErrorMsg());
        }
        throw new BusinessException(COMB_REQUEST_NO_NULL.getDesc());
    }

    private List<CombineEntrustDTO> filterCombineEntrust(List<CombineEntrustDTO> combineEntrustDTOs) {
        return combineEntrustDTOs.stream().filter(entrust -> !CombEntrustStatusEnum.ORDER_WITHDRAW.getCode()
                .equals(entrust.getComb_request_status())).collect(Collectors.toList());
    }

    private boolean checkResult(BaseResult<CombineEntrustResp> combEntrustResult) {
        return combEntrustResult.isSuccess() && combEntrustResult.getData() != null
                && !CollectionUtils.isEmpty(combEntrustResult.getData().getRows());
    }
}
