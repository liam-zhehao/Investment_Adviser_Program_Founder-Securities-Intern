package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 投资需求
 */
@Data
@ApiModel(value = "InvestDemandVO", description = "投资需求列表")
public class InvestDemandVO {
    @ApiModelProperty(value = "投资需求的选项",required = true)
    private String item;

    @ApiModelProperty(value = "投资需求的描述",required = true)
    private String desc;
}
