package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InvestOrganPageVO", description = "投顾专区信息")
public class InvestOrganPageVO {

    @ApiModelProperty(value = "标题", required = true)
    private String detailPageTitle;

    @ApiModelProperty(value = "投顾机构logo", required = true)
    private String investOrganLogo;

    @ApiModelProperty(value = "详情页描述", required = true)
    private String detailPageDesc;

}
