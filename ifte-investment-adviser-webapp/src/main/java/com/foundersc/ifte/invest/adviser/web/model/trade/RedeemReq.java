package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提交赎回
 */
@Data
@ApiModel(value = "RedeemReq", description = "赎回请求")
public class RedeemReq {

    @ApiModelProperty(value = "组合编号", required = true)
    @NotBlank
    private String combineCode;

    @ApiModelProperty(value = "赎回比例", required = true)
    @NotNull
    private BigDecimal redeemRatio;
}
