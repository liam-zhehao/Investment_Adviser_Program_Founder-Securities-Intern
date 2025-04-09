package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CombAssetInfo implements Serializable {
    /**
     * 是否有投顾资产
     */
    private boolean hasCombAsset;

    /**
     * 是否有投顾账户
     */
    private boolean hasInvestorAccount;

    /**
     * 投顾总资产
     */
    private BigDecimal totalCombAsset;

    /**
     * 总资产
     */
    private ClientTotalAsset clientTotalAsset;

    /**
     * 组合资产列表
     */
    private List<ClientCombAsset> clientCombAssets;


    /**
     * 在途组合委托信息
     */
    private List<CombineEntrust> combineEntrusts;

}
