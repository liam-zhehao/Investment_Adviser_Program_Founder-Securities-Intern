package com.foundersc.ifte.invest.adviser.web.model.combine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 持仓类型及占比
 */
@Data
@ApiModel(value = "PositionTypeRatioVO", description = "持仓类型及占比")
public class PositionTypeRatioVO {

    @ApiModelProperty("产品投资类别")
    private String prodInvestType;

    @ApiModelProperty("产品投资类别名称")
    private String prodInvestTypeName;

    @ApiModelProperty("占比")
    private String ratio;

    @ApiModelProperty("占比描述：百分比")
    private String ratioDesc;
}
