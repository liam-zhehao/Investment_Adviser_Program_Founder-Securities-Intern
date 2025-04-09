package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author anhuan
 * @date 2022/9/13 下午10:36
 */
@Data
@ApiModel(value = "SignEpaperReq", description = "签署协议请求")
public class SignEpaperReq {

    @ApiModelProperty(value = "组合编号", required = true)
    @NotBlank
    private String combineCode;
}
