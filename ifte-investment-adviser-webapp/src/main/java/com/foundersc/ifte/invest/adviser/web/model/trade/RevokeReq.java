package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author anhuan
 * @date 2022/9/13 下午10:29
 */
@Data
@ApiModel(value = "RevokeReq", description = "撤单请求")
public class RevokeReq {

    @ApiModelProperty(value = "组合编号", required = true)
    @NotBlank
    private String combineCode;

    @ApiModelProperty(value = "原组合转入申请编号", required = true)
    @NotBlank
    private String origCombRequestNo;
}
