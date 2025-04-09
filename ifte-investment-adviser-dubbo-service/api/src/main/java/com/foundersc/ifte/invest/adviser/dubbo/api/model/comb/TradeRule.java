package com.foundersc.ifte.invest.adviser.dubbo.api.model.comb;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CopywritingEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易规则
 *
 * @author wangfuwei
 * @date 2022/9/27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeRule implements Serializable {

    private static final long serialVersionUID = -8193778842142234838L;
    /**
     * 转入时间轴
     */
    private List<TimelineItem> buyTimeline;

    /**
     * 转出时间轴
     */
    private List<TimelineItem> redeemTimeline;

    /**
     * 基本文案，在一级页面展示，例如：投顾服务费，买入、卖出费用
     */
    private List<Copywriting> basicCopywritings;

    /**
     * 扩展文案，在二级页面展示，例如：买入规则、卖出规则、调仓规则、费用说明
     */
    private List<Copywriting> otherCopywritings;

    /**
     * 时间轴
     */
    @Data
    @AllArgsConstructor
    public static class TimelineItem implements Serializable {
        private static final long serialVersionUID = -7005294331015764472L;

        /**
         * 例如：转入
         */
        private String title;

        /**
         * 例如：今日15点前
         */
        private String text;
    }

    /**
     * 文案
     */
    @Data
    @AllArgsConstructor
    public static class Copywriting implements Serializable {

        private static final long serialVersionUID = -8503906627701388337L;
        /**
         * 类型
         */
        private String type;
        /**
         * 标题
         */
        private String title;
        /**
         * 内容
         */
        private String text;
        /**
         * 内容
         */
        private List<Copywriting> contents;

        public Copywriting(CopywritingEnum copywritingEnum) {
            this.type = copywritingEnum.getCode();
            this.title = copywritingEnum.getDesc();
            this.contents = new ArrayList<>();
        }

        public Copywriting() {
            this.contents = new ArrayList<>();
        }
    }
}
