package com.foundersc.ifte.invest.adviser.web.model.combine;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.TradeDirectionEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 收益率走势
 */
@Data
@ApiModel(value = "IncomeRatioChartVO", description = "收益率/涨跌幅走势")
public class IncomeRatioChartVO {

    @ApiModelProperty(value = "时间区间id", allowableValues = "range[1, 10]")
    private Integer rangeId;

    @ApiModelProperty(value = "时间区间描述", allowableValues = "近一月,近3月,近6月,近1年,成立来")
    private String rangeDesc;

    @ApiModelProperty("组合涨跌幅曲线")
    private IncomeRatioLineVO combineLine;

    @ApiModelProperty("基准涨跌幅曲线")
    private IncomeRatioLineVO benchmarkLine;

    @Data
    @ApiModel(value = "IncomeRatioLineVO", description = "收益率走势曲线")
    public static class IncomeRatioLineVO {

        @ApiModelProperty("涨跌幅")
        private BigDecimal incomeRatio;

        @ApiModelProperty(value = "涨跌幅展示描述", example = "+2.15%")
        private String incomeRatioDesc;

        @ApiModelProperty("涨跌幅曲线的点")
        private List<IncomeRatioVO> incomeRatios;

        public IncomeRatioLineVO() {
            this.incomeRatios = new ArrayList<>();
        }
    }

    @Data
    @ApiModel(value = "IncomeRatioVO", description = "收益率/涨跌幅")
    public static class IncomeRatioVO {

        @ApiModelProperty("横坐标：日期")
        private String date;

        @ApiModelProperty("格式日期：yyyy-MM-dd")
        private String formatDate;

        @ApiModelProperty("纵坐标：涨跌幅")
        private BigDecimal ratio;

        @ApiModelProperty(value = "纵坐标：涨跌幅描述", example = "+2.15%")
        private String ratioDesc;

        @ApiModelProperty(value = "当日涨跌幅")
        private BigDecimal todayIncomeRatio;

        @ApiModelProperty(value = "当日涨跌幅描述")
        private String todayIncomeRatioDesc;

        @ApiModelProperty(value = "累计净值")
        private BigDecimal sumNetValue;

        @ApiModelProperty("买卖方向：0无交易 1买 2卖 3买卖")
        private Integer direction;

        public IncomeRatioVO() {
            this.direction = TradeDirectionEnum.NONE.getCode();
        }
    }
}
