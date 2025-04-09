package com.foundersc.ifte.invest.adviser.dubbo.service.impl;

import com.foundersc.ifc.portfolio.t2.request.CombAgreementReq;
import com.foundersc.ifc.portfolio.t2.request.CombShareReq;
import com.foundersc.ifc.portfolio.t2.request.CombineAssetReq;
import com.foundersc.ifc.portfolio.t2.request.v2.trade.QueryAssetFetchExtReq;
import com.foundersc.ifc.portfolio.t2.response.invest.*;
import com.foundersc.ifc.portfolio.t2.response.v2.trade.QueryAssetFetchExtResp;
import com.foundersc.ifc.portfolio.t2.service.ClientAssetService;
import com.foundersc.ifc.portfolio.t2.service.v2.TradeService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.CombAgreement;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.ClientCombAsset;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.ClientTotalAsset;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombFetchAsset;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.position.CombShareItem;
import com.foundersc.ifte.invest.adviser.dubbo.service.ClientAssetQueryService;
import com.foundersc.ifte.invest.adviser.dubbo.util.ObjectCopyUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.*;
import static com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants.INIT_PAGE_NO;
import static com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants.PAGE_SIZE;

@Service
@Slf4j
public class ClientAssetQueryServiceImpl implements ClientAssetQueryService {

    @Autowired // todo xml配置
    private ClientAssetService clientAssetService;

    @Autowired
    private TradeService tradeService;

    public static int ONCE_QUERY_PAGE_SIZE = 500;

    @Override
    public List<CombAgreement> getCombAgreementInfo(SimpleAccount simpleAccount) {
        List<CombAgreement> combAgreements = new ArrayList<>();
        List<CombAgreementDTO> combAgreementDTOs = queryCombAgreement(simpleAccount);
        if (!CollectionUtils.isEmpty(combAgreementDTOs)) {
            ObjectCopyUtil.copyCombAgreement(combAgreementDTOs, combAgreements);
        }
        return combAgreements;
    }

    @Override
    public ClientTotalAsset getClientTotalAsset(SimpleAccount simpleAccount) {
        BaseResult<TotalAssetResp> totalAssetResult = clientAssetService.getClientTotalAsset(simpleAccount);
        if (!totalAssetResult.isSuccess()) {
            log.error(QUERY_TOTAL_ASSET_ERROR + "[total asset] client {} query comb total asset error", simpleAccount.getClientId(), totalAssetResult.getErrorMsg());
            throw new BusinessException(ErrorCodeEnum.QUERY_TOTAL_ASSET_ERROR);
        }

        ClientTotalAsset clientTotalAsset = new ClientTotalAsset();
        if (totalAssetResult.getData() != null) {
            ObjectCopyUtil.copyTotalAsset(totalAssetResult.getData(), clientTotalAsset);
        }

        return clientTotalAsset;
    }

    @Override
    public List<ClientCombAsset> getClientCombAsset(SimpleAccount simpleAccount, String combineCode) {
        List<ClientCombAsset> clientCombAssets = new ArrayList<>();
        List<CombineAssetDTO> combineAssetDTOs = queryCombAsset(simpleAccount, combineCode);
        if (!CollectionUtils.isEmpty(combineAssetDTOs)) {
            ObjectCopyUtil.copyCombAsset(combineAssetDTOs, clientCombAssets);
        }
        return clientCombAssets;
    }

    @Override
    public List<CombShareItem> getCombPositionShares(SimpleAccount simpleAccount, String combineCode) {
        if (StringUtils.isEmpty(combineCode)) {
            return new ArrayList<>();
        }
        CombShareReq combShareReq = new CombShareReq();
        combShareReq.setCombineCode(combineCode);
        combShareReq.setPageSize(100);
        BaseResult<CombShareResp> combShareResult = clientAssetService.getCombShareInfo(simpleAccount, combShareReq);
        if (!combShareResult.isSuccess()) {
            log.error(QUERY_COMB_SHARE_ERROR + "[comb share] clientId {} query combineCode {} error {}.", simpleAccount.getClientId(), combineCode, combShareResult.getErrorMsg());
            throw new BusinessException(QUERY_COMB_SHARE_ERROR);
        }
        if (combShareResult.getData() == null || CollectionUtils.isEmpty(combShareResult.getData().getRows())) {
            log.info("[comb share] clientId {} query combineCode {} result is null", simpleAccount.getClientId(), combineCode);
            return new ArrayList<>();
        }
        List<CombShareDTO> combShareDTOs = combShareResult.getData().getRows();
        List<CombShareItem> shareItems = new ArrayList<>(combShareDTOs.size());
        ObjectCopyUtil.copyCombShareItems(combShareDTOs, shareItems);
        // 根据成分金额倒序，产品代码升序排序
        shareItems = shareItems.stream().sorted(Comparator.comparing(CombShareItem::getProdMarketValue).reversed().thenComparing(CombShareItem::getFundCode)).collect(Collectors.toList());
        log.info("[comb share] clientId {} prod size:{}", simpleAccount.getClientId(), shareItems.size());
        return shareItems;
    }

    @Override
    public CombFetchAsset getCombFetchAsset(SimpleAccount simpleAccount, String combineCode) {
        Preconditions.checkArgument(!StringUtils.isEmpty(combineCode), PARAM_NULL.getDesc());
        QueryAssetFetchExtReq fetchExtReq = new QueryAssetFetchExtReq();
        fetchExtReq.setCombineCode(combineCode);
        BaseResult<QueryAssetFetchExtResp> result = tradeService.queryAssetFetchExt(simpleAccount, fetchExtReq);
        if (!result.isSuccess()) {
            log.error(QUERY_COMB_FETCH_ASSET_ERROR + "[comb fetch asset] clientId {} get comb {} asset error {}.", simpleAccount.getClientId(), combineCode, result.getErrorMsg());
            throw new BusinessException(QUERY_COMB_FETCH_ASSET_ERROR);
        }

        if (result.getData() == null) {
            log.info("[comb fetch asset] clientId {} get comb {} asset is null.");
            return null;
        }
        QueryAssetFetchExtResp fetchExtResp = result.getData();
        CombFetchAsset combFetchAsset = new CombFetchAsset();
        ObjectCopyUtil.copyCombFetchAsset(fetchExtResp, combFetchAsset);
        return combFetchAsset;
    }

    /**
     * 翻页查询柜台组合资产
     *
     * @param simpleAccount
     * @return
     */
    private List<CombineAssetDTO> queryCombAsset(SimpleAccount simpleAccount, String combineCode) {
        CombineAssetReq combineAssetReq = new CombineAssetReq();
        if (!StringUtils.isEmpty(combineCode)) {
            combineAssetReq.setCombineCode(combineCode);
        }
        BaseResult<CombineAssetResp> combineAssetResult = clientAssetService.getClientCombAsset(simpleAccount, combineAssetReq);
        if (!combineAssetResult.isSuccess()) {
            if (combineAssetResult.getData() != null) {
                log.error(QUERY_COMB_ASSET_ERROR + "[comb asset] clientId {} query comb Asset error {}", simpleAccount.getClientId(), combineAssetResult.getData().getError_info());
            } else {
                log.error(QUERY_COMB_ASSET_ERROR + "[comb asset] clientId {} query comb Asset error {}", simpleAccount.getClientId(), combineAssetResult.getErrorMsg());
            }
            throw new BusinessException(ErrorCodeEnum.QUERY_COMB_ASSET_ERROR);
        }

        CombineAssetResp combineAssetResp = combineAssetResult.getData();
        if (combineAssetResp != null && !CollectionUtils.isEmpty(combineAssetResp.getRows())) {
            log.info("[comb asset] clientId {} query combineAssets {} success.", combineCode);
            return combineAssetResp.getRows();
        }
        return null;
    }

    /**
     * 翻页查询柜台组合资产
     *
     * @param simpleAccount
     * @return
     */
    private List<CombineAssetDTO> queryCombAssetWithPage(SimpleAccount simpleAccount, String combineCode) {
        List<CombineAssetDTO> combineAssets = new ArrayList<>();
        CombineAssetReq combineAssetReq = new CombineAssetReq(INIT_PAGE_NO, PAGE_SIZE);
        if (!StringUtils.isEmpty(combineCode)) {
            combineAssetReq.setCombineCode(combineCode);
        }
        BaseResult<CombineAssetResp> combineAssetResult = null;
        while (true) {
            try {
                combineAssetResult = clientAssetService.getClientCombAsset(simpleAccount, combineAssetReq);
                if (!combineAssetResult.isSuccess()) {
                    if (combineAssetResult.getData() != null) {
                        log.error("[comb asset] clientId {} query comb Asset error {}", simpleAccount.getClientId(), combineAssetResult.getData().getError_info());
                    } else {
                        log.error("[comb asset] clientId {} query comb Asset error {}", simpleAccount.getClientId(), combineAssetResult.getErrorMsg());
                    }
                    break;
                }
                CombineAssetResp combineAssetResp = combineAssetResult.getData();
                if (combineAssetResp == null) {
                    log.info("[comb asset] clientId {} comb Asset size is 0", simpleAccount.getClientId());
                    break;
                }
                if (!CollectionUtils.isEmpty(combineAssetResp.getRows())) {
                    combineAssets.addAll(combineAssetResp.getRows());
                    int nextPageNo = combineAssetResp.getCurrent_page() + 1;
                    combineAssetReq.setPageNo(nextPageNo);
                } else {
                    break;
                }
            } catch (Exception e) {
                // todo 加监控
                log.error("[comb asset] clientId {} query exception", simpleAccount.getClientId(), e);
                return combineAssets;
            }
        }
        log.info("[comb asset] clientId {} query combineAssets size:{}", combineAssets.size());
        return combineAssets;

    }

    /**
     * 查询柜台组合协议信息
     *
     * @param simpleAccount
     * @return
     */
    private List<CombAgreementDTO> queryCombAgreement(SimpleAccount simpleAccount) {
        List<CombAgreementDTO> combAgreements = new ArrayList<>();
        CombAgreementReq combAgreementReq = new CombAgreementReq(INIT_PAGE_NO, PAGE_SIZE);
        BaseResult<CombAgreementResp> combAgrResult = null;
        while (true) {
            try {
                combAgrResult = clientAssetService.getCombAgreement(simpleAccount, combAgreementReq);
                if (!combAgrResult.isSuccess()) {
                    if (combAgrResult.getData() != null) {
                        log.error("[comb agreement] clientId {} query agreement error:{}", simpleAccount.getClientId(), combAgrResult.getData().getError_info());
                    } else {
                        log.error("[comb agreement] clientId {} query agreement error:{}", simpleAccount.getClientId(), combAgrResult.getErrorMsg());
                    }
                    break;
                }
                CombAgreementResp combAgrResp = combAgrResult.getData();
                if (combAgrResp == null) {
                    log.info("[comb agreement] clientId {} comb agreement size is 0", simpleAccount.getClientId());
                    break;
                }
                if (!CollectionUtils.isEmpty(combAgrResp.getRows())) {
                    combAgreements.addAll(combAgrResp.getRows());
                    int nextPageNo = combAgrResp.getCurrent_page() + 1;
                    combAgreementReq.setPageNo(nextPageNo);
                } else {
                    break;
                }
            } catch (Exception e) {
                log.error("[comb agreement] clientId {} query exception", simpleAccount.getClientId(), e);
                return combAgreements;
            }
        }
        log.info("[comb agreement] clientId {} query combAgreements size:{}", combAgreements.size());
        return combAgreements;
    }
}
