package com.foundersc.ifte.invest.adviser.web.model.combine;

import com.foundersc.ifte.invest.adviser.web.util.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 组合持仓
 */
@ApiModel(value = "CombinePositionVO", description = "组合持仓")
@Data
public class CombinePositionVO {

    @ApiModelProperty("更新日期：取上一个交易日")
    private Integer updateDate;

    @ApiModelProperty("更新日期（格式化）")
    private String updateDateDesc;

    @ApiModelProperty("组合持仓饼状图")
    private List<PositionTypeRatioVO> typeRatios;

    @ApiModelProperty(value = "持仓明细", notes = "按照持仓比例倒序")
    private List<PositionProdRatioVO> prodRatios;

    public String getUpdateDateDesc() {
        return DateUtil.formatYearMonthDay(updateDate);
    }
}
