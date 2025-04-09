package com.foundersc.ifte.invest.adviser.web.model.combine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 策略特点
 */
@Data
@ApiModel(value = "StrategyTraitVO", description = "策略特点信息")
public class StrategyTraitVO {
    @ApiModelProperty(value = "策略特点标题", required = true)
    private String title;

    @ApiModelProperty(value = "策略特点详情", required = true)
    private String detail;
}
