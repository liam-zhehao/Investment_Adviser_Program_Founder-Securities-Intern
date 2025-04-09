package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InvestOrganVO", description = "投顾机构信息")
public class InvestOrganVO {

    @ApiModelProperty(value = "投顾机构编号", required = true)
    private String investOrganNo;

    @ApiModelProperty(value = "投顾机构名称", required = true)
    private String investOrganName;

    @ApiModelProperty(value = "投顾机构logo", required = true)
    private String investOrganLogo;

    @ApiModelProperty(value = "banner图片url", required = true)
    private String bannerPic;

    @ApiModelProperty(value = "详情页标题", required = true)
    private String detailPageTitle;

    @ApiModelProperty(value = "详情页描述", required = true)
    private String detailPageDesc;

}
