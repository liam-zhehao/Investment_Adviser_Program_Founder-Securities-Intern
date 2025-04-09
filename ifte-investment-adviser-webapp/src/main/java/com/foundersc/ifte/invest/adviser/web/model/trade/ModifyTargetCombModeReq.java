package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author wangfuwei
 * @date 2023/8/29
 */
@Data
@ApiModel(value = "ModifyTargetCombModeReq", description = "修改目标盈续期方式请求")
public class ModifyTargetCombModeReq {
    @ApiModelProperty(value = "目标盈组合代码", required = true)
    @NotBlank
    private String combineCode;

    @ApiModelProperty(value = "续期方式", required = true)
    @NotBlank
    private String continueMode;
}
