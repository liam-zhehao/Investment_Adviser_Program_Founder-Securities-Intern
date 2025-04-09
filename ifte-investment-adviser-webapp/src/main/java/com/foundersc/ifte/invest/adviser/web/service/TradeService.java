package com.foundersc.ifte.invest.adviser.web.service;

import com.foundersc.ifc.portfolio.t2.response.v2.trade.QueryAgreementResp;
import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import com.foundersc.ifte.invest.adviser.web.model.trade.*;

import java.util.List;

/**
 * 交易相关
 *
 * @author wangfuwei
 * @date 2022/9/29
 */
public interface TradeService {
    /**
     * 交易前检查
     *
     * @param combineCode
     * @param {@link      TradeDirectionEnum}
     * @return
     */
    TradePreCheckResultVO preCheck(String combineCode);


    TradePreCheckResultVO newPreCheck(String combineCode, Integer tradeType);


    /**
     * 是否已经签约组合
     *
     * @param combineCode
     * @return
     */
    boolean hasSignedComb(String combineCode);

    /**
     * 当前时间目标盈组合是否能购买
     * @return
     */
    Integer canTargetCombPurchase(CombineInfoVO combineInfo);

    /**
     * 是否已经签约组合
     *
     * @param queryAgreementResp
     * @return
     */
    boolean hasSignedComb(QueryAgreementResp queryAgreementResp);

    /**
     * 是否在解约中
     *
     * @param combineCode
     * @return
     */
    boolean isCancellingComb(String combineCode);

    /**
     * 是否在解约中
     *
     * @param queryAgreementResp
     * @return
     */
    boolean isCancellingComb(QueryAgreementResp queryAgreementResp);

    /**
     * 组合适当性校验
     *
     * @param combineCode
     * @return
     */
    EligibilityCheckResultVO checkEligibility(String combineCode);

    /**
     * 购买页信息
     *
     * @param combineCode
     * @return
     */
    PurchaseInfoVO getPurchaseInfo(String combineCode);

    /**
     * 电子协议列表
     *
     * @param combineCode
     * @return
     */
    List<EpaperVO> listEpaper(String combineCode);

    /**
     * 电子协议签署
     *
     * @param combineCode
     * @return
     */
    Boolean signEpaper(String combineCode);

    /**
     * 购买
     *
     * @param purchaseReq
     * @return
     */
    PurchaseResultVO purchase(PurchaseReq purchaseReq);

    /**
     * 撤单
     *
     * @param revokeReq
     * @return
     */
    RevokeResultVO revoke(RevokeReq revokeReq);

    /**
     * 赎回页面信息
     *
     * @param combineCode
     * @return
     */
    RedeemInfoVO getRedeemInfo(String combineCode);

    /**
     * 赎回
     *
     * @param redeemReq
     * @return
     */
    RedeemResultVO redeem(RedeemReq redeemReq);

    /**
     * 可用余额
     *
     * @return
     */
    EnableBalanceVO getEnableBalance();

    /**
     * 校验交易时间
     * @param combineCode
     * @return
     */
    TimeCheckResultVO checkTime(String combineCode);
}
