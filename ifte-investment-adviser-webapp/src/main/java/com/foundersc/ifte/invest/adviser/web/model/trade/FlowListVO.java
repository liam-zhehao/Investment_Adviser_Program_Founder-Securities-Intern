package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowListVO implements Serializable {
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("描述")
    private String desc;

    @ApiModelProperty("完成标志")
    private boolean complete;

}
