package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 撤单
 */
@ApiModel(value = "RevokeResultVO", description = "撤单结果")
@Data
public class RevokeResultVO {
    @ApiModelProperty("组合申请编号")
    private String combRequestNo;
}
