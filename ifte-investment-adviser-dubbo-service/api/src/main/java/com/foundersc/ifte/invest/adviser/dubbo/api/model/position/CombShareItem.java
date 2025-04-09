package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import lombok.Data;

import java.io.Serializable;

/**
 * 组合成分持仓明细
 */
@Data
public class CombShareItem implements Serializable {
    /**
     * 基金名称
     */
    private String fundName;

    /**
     * 基金代码
     */
    private String fundCode;

    /**
     * 产品市值展示
     */
    private String prodMarketValueFormat;

    /**
     * 产品市值
     */
    private Double prodMarketValue;
}
