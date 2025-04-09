package com.foundersc.ifte.invest.adviser.web.model.homepage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文案
 */
@Data
@ApiModel(value = "CopywritingVO", description = "文案")
public class CopywritingVO {
    @ApiModelProperty(value = "标题",required = true)
    private String title;

    @ApiModelProperty(value = "副标题",required = true)
    private String subTitle;
}
