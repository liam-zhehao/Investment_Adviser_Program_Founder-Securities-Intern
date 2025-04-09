package com.foundersc.ifte.invest.adviser.web.model.homepage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 投顾机构
 */
@Data
@ApiModel(value = "InvestOrgVO", description = "投顾机构")
public class InvestOrgVO {
    @ApiModelProperty("投顾机构编码")
    private String code;

    @ApiModelProperty("投顾机构名称")
    private String name;

    @ApiModelProperty("投顾机构在列表中展示名称")
    private String displayName;

    @ApiModelProperty("投顾机构简介")
    private String desc;

    @ApiModelProperty("卡片展示")
    private InvAdviserCardVO card;
}
