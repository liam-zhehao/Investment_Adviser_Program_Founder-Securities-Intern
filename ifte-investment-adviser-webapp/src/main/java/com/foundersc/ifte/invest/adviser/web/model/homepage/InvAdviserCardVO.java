package com.foundersc.ifte.invest.adviser.web.model.homepage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 投顾机构卡片
 */
@Data
@ApiModel(value = "InvAdviserCardVO", description = "投顾机构卡片")
public class InvAdviserCardVO {
    @ApiModelProperty("图片url")
    private String imgUrl;

    @ApiModelProperty("链接地址")
    private String link;
}
