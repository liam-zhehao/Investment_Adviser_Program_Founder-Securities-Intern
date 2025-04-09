package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 文案
 */
@Data
@ApiModel(value = "CopywritingVO", description = "文案")
@AllArgsConstructor
public class CopywritingVO {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private List<CopywritingVO> contents;

    public CopywritingVO() {
        this.contents = new ArrayList<>();
    }

}