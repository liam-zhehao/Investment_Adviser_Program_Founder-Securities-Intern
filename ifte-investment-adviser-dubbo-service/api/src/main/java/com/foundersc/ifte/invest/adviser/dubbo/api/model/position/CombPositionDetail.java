package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.CombinePosition;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合持仓详细信息
 * 包括持仓收益和组合持仓成分产品，买入卖出标记
 */
@Data
public class CombPositionDetail implements Serializable {

    /**
     * 组合资产信息
     */
    private ClientCombAsset clientCombAsset;

    /**
     * 组合成分明细
     */
    private List<CombShareItem> shareItems;

    /**
     * 买入按钮标记
     */
    private boolean canBuy;

    /**
     * 无法买入提示
     */
    private String cannotBuyTip;

    /**
     * 卖出按钮标记
     */
    private boolean canRedeem;

    /**
     * 无法卖出提示
     */
    private String cannotRedeemTip;

    /**
     * 交易提示信息
     */
    private String tradeTip;

    /**
     * 组合持仓饼状图
     */
    private List<CombinePosition.PositionTypeRatio> typeRatios;

    /**
     * 组合成份更新日期
     */
    private Integer positionTypeRatioUpdateDate;

    /**
     * 目标盈运作流程
     */
    private TargetCombOperationPeriod targetCombOperationPeriod;

    public CombPositionDetail() {
        this.canBuy = true;
        this.canRedeem = true;
        this.shareItems = new ArrayList<>();
        this.typeRatios = new ArrayList<>();
    }

    /**
     * 目标盈运作流程
     */
    @Data
    public static class TargetCombOperationPeriod implements Serializable {
        private static final long serialVersionUID = 1611418922895869869L;
        /**
         * 策略终止日期描述，例如：策略到期日2024-08-06
         */
        private String combEndDateDesc;

        /**
         * 阶段日期
         */
        private List<Period> periods;

        /**
         * 当前日期
         */
        private Integer initDate;

        /**
         * 止盈生效日期
         */
        private Integer profitValidDate;

        /**
         * 是否处于参与期
         */
        private boolean inParticipationPeriod;

        /**
         * 是否处于观察期
         */
        private boolean inObservationPeriod;

        /**
         * 是否处于止盈期
         */
        private boolean inStopProfitPeriod;
    }

    /**
     * 阶段名称即开始日期
     */
    @Data
    @AllArgsConstructor
    public static class Period implements Serializable {
        private static final long serialVersionUID = -6103044108294924412L;
        /**
         * 名称，例如：参与期
         */
        private String name;
        /**
         * 开始日期
         */
        private Integer startDate;
        /**
         * 开始时间
         */
        private Integer startTime;
        /**
         * 开始日期（格式化），例如：2023-07-31
         */
        private String startDateFormat;
        /**
         * 进度百分比
         */
        private BigDecimal percent = new BigDecimal("0");
        /**
         * 结束日期
         */
        private Integer endDate;
        /**
         * 结束时间
         */
        private Integer endTime;
        /**
         * 操作及是否可以操作
         */
        private List<Operation> operations;
        /**
         * 是否处于这个阶段
         */
        private boolean inThisPeriod;

        public Period() {
            this.operations = new ArrayList<>();
        }
    }

    /**
     * 操作及是否允许
     */
    @Data
    @AllArgsConstructor
    public static class Operation implements Serializable {
        private static final long serialVersionUID = -8823335870838005589L;
        /**
         * 操作名称
         */
        private String name;
        /**
         * 是否可以操作
         */
        private boolean canOperate;

        public Operation() {}
    }
}
