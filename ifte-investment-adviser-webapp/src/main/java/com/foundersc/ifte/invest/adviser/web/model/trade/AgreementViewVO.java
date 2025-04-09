package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "AgreementViewVO", description = "协议")
public class AgreementViewVO {

    @ApiModelProperty("协议名称")
    private String title;

    @ApiModelProperty("协议url")
    private String url;
}
