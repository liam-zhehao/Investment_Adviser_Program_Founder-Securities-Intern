package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 转入
 */
@Data
@ApiModel(value = "PurchaseReq", description = "转入请求")
public class PurchaseReq {

    @ApiModelProperty(value = "组合编号", required = true)
    @NotBlank
    private String combineCode;

    @ApiModelProperty(value = "金额", required = true)
    @NotNull
    private BigDecimal entrustAmount;

    @ApiModelProperty(value = "续期方式", required = false)
    private String continueMode;

    @ApiModelProperty(value = "自动转账协议签署标志", required = false)
    private Integer autoTransFlag;
}
