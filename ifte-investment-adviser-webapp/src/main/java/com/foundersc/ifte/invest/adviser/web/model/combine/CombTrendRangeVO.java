package com.foundersc.ifte.invest.adviser.web.model.combine;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombTrendRangeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行情趋势区间
 */
@Data
@ApiModel(value = "CombTrendRangeVO", description = "行情趋势区间")
@NoArgsConstructor
public class CombTrendRangeVO {
    @ApiModelProperty("区间id")
    private Integer rangeId;

    @ApiModelProperty("区间描述")
    private String rangeDesc;

    public CombTrendRangeVO(CombTrendRangeEnum combTrendRangeEnum) {
        this.rangeId = combTrendRangeEnum.getRangeId();
        this.rangeDesc = combTrendRangeEnum.getRangeDesc();
    }
}
