package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TargetKycInfo {
    @ApiModelProperty(value = "标题", required = true)
    private String kycTitle;

    @ApiModelProperty(value = "详情", required = true)
    private String kycDetail;
}
