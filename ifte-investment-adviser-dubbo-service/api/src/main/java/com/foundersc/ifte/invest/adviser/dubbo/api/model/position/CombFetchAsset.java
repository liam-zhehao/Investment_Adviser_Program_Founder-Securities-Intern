package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CombFetchAsset implements Serializable {

    private String clientId;

    /**
     * 组合代码
     */
    private String combineCode;

    /**
     * 组合资产
     */
    private BigDecimal combineAsset;

    /**
     * 最小可取金额
     */
    private BigDecimal minFetchBalance;

    /**
     * 最大可取金额
     */
    private BigDecimal maxFetchBalance;

    /**
     * 最小可取比例
     */
    private BigDecimal minFetchRatio;

    /**
     * 最大可取比例
     */
    private BigDecimal maxFetchRatio;

    /**
     * 可取市值
     */
    private BigDecimal fetchMarketValue;

    /**
     * 是否允许赎回
     */
    private String enRedeemFlag;

    /**
     * 是否允许全部赎回标志
     */
    private String allowAllRedeemFlag;

    /**
     * 可全部赎回比例
     */
    private BigDecimal allRedeemRatio;
}
