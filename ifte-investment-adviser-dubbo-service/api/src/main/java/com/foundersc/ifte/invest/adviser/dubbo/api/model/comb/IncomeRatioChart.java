package com.foundersc.ifte.invest.adviser.dubbo.api.model.comb;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombTrendRangeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.TradeDirectionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 收益率/涨跌幅走势
 *
 * @author wangfuwei
 * @date 2022/9/26
 */
@Data
@NoArgsConstructor
public class IncomeRatioChart implements Serializable {
    private static final long serialVersionUID = 5961818046824720865L;
    /**
     * 时间区间id
     */
    private Integer rangeId;

    /**
     * 时间区间描述
     */
    private String rangeDesc;

    /**
     * 组合涨跌幅曲线
     */
    private IncomeRatioLine combineLine;

    /**
     * 基准涨跌幅曲线
     */
    private IncomeRatioLine benchmarkLine;

    public IncomeRatioChart(CombTrendRangeEnum combTrendRangeEnum) {
        this.rangeId = combTrendRangeEnum.getRangeId();
        this.rangeDesc = combTrendRangeEnum.getRangeDesc();
        this.combineLine = new IncomeRatioLine();
        this.benchmarkLine = new IncomeRatioLine();
    }

    /**
     * 收益率走势曲线
     */
    @Data
    public static class IncomeRatioLine implements Serializable {

        private static final long serialVersionUID = 5802992711931731972L;
        /**
         * 涨跌幅
         */
        private BigDecimal incomeRatio;

        /**
         * 涨跌幅展示描述，如+2.15%
         */
        private String incomeRatioDesc;

        /**
         * 收益率曲线的点
         */
        private List<IncomeRatio> incomeRatios;

        public IncomeRatioLine() {
            this.incomeRatio = new BigDecimal("0");
            this.incomeRatioDesc = "0%";
            this.incomeRatios = new ArrayList<>();
        }
    }

    /**
     * 收益率/涨跌幅
     */
    @Data
    public static class IncomeRatio implements Serializable {

        private static final long serialVersionUID = 4698532811664152832L;
        /**
         * 横坐标：日期
         */
        private Integer date;

        /**
         * 日期格式化
         */
        private String formatDate;

        /**
         * 纵坐标：收益率
         */
        private BigDecimal ratio;

        /**
         * 纵坐标：收益率描述，如+2.15%
         */
        private String ratioDesc;

        /**
         * 涨跌幅
         */
        private BigDecimal todayIncomeRatio;

        /**
         * 涨跌幅描述
         */
        private String todayIncomeRatioDesc;

        /**
         * 累计净值
         */
        private BigDecimal sumNetValue;

        /**
         * 买卖方向：0无交易 1买 2卖 3买卖
         */
        private Integer direction;

        public IncomeRatio() {
            this.direction = TradeDirectionEnum.NONE.getCode();
        }
    }
}
