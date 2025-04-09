package com.foundersc.ifte.invest.adviser.dubbo.api.model.comb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合持仓
 *
 * @author wangfuwei
 * @date 2022/9/27
 */
@Data
@AllArgsConstructor
public class CombinePosition implements Serializable {
    private static final long serialVersionUID = -5935912449023228618L;
    /**
     * 更新日期
     */
    private Integer updateDate;

    /**
     * 组合持仓饼状图
     */
    private List<PositionTypeRatio> typeRatios;

    /**
     * 持仓明细，按照持仓比例倒序
     */
    private List<PositionProdRatio> prodRatios;

    public CombinePosition() {
        typeRatios = new ArrayList<>();
        prodRatios = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionTypeRatio implements Serializable {
        private static final long serialVersionUID = -6479139438113533351L;
        /**
         * 产品成分类别
         */
        private String prodInvestType;

        /**
         * 产品成分类别名称
         */
        private String prodInvestTypeName;

        /**
         * 占比
         */
        private BigDecimal ratio;

        /**
         * 占比描述：百分比
         */
        private String ratioDesc;
    }

    @Data
    public static class PositionProdRatio implements Serializable {
        private static final long serialVersionUID = 6350576001658858958L;
        /**
         * ta编号
         */
        private String prodtaNo;

        /**
         * 产品编码
         */
        private String prodCode;

        /**
         * 产品名称
         */
        private String prodName;

        /**
         * 产品全称
         */
        private String prodFullName;

        /**
         * 产品占比
         */
        private BigDecimal productRatio;

        /**
         * 产品占比描述：百分比
         */
        private String productRatioDesc;

        /**
         * 产品投资类别代码
         */
        private String prodInvestType;

        /**
         * 产品投资类别名称
         */
        private String prodInvestTypeName;
    }
}
