package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wangfuwei
 * @date 2023/10/17
 */
@Data
@ApiModel(value = "FlagAndTipVO", description = "标志及提示")
@AllArgsConstructor
public class FlagAndTipVO {
    @ApiModelProperty("标志")
    private Boolean flag;

    @ApiModelProperty("flag为false时的提示信息")
    private String tip;

    public FlagAndTipVO() {
        this.flag = true;
    }
}
