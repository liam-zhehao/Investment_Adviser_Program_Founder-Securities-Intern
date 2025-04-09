package com.foundersc.ifte.invest.adviser.web.model.combine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 持仓产品及占比
 */
@Data
@ApiModel(value = "PositionProdRatioVO", description = "持仓产品及占比")
public class PositionProdRatioVO {

    @ApiModelProperty("ta编号")
    private String prodtaNo;

    @ApiModelProperty("产品编码")
    private String prodCode;

    @ApiModelProperty("产品名称")
    private String prodName;

    @ApiModelProperty("产品全称")
    private String prodFullName;

    @ApiModelProperty("产品占比")
    private String productRatio;

    @ApiModelProperty("产品投资类别")
    private String prodInvestType;

    @ApiModelProperty("产品投资类别名称")
    private String prodInvestTypeName;
}
