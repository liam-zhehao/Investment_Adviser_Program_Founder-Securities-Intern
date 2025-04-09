package com.foundersc.ifte.invest.adviser.web.model.combine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 交易规则
 */
@Data
@ApiModel(value = "TradeRuleVO", description = "交易规则")
@AllArgsConstructor
@NoArgsConstructor
public class TradeRuleVO {

    @ApiModelProperty("转入时间轴")
    private List<TimelineItem> buyTimeline;

    @ApiModelProperty("转出时间轴")
    private List<TimelineItem> redeemTimeline;

    @ApiModelProperty("基本文案，在一级页面展示，例如：投顾服务费，转入、转出费用")
    private List<CopywritingVO> basicCopywritings;

    @ApiModelProperty("扩展文案，在二级页面展示，例如：转入规则、转出规则、调仓规则、费用说明")
    private List<CopywritingVO> otherCopywritings;

    /**
     * 时间轴
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimelineItem implements Serializable {
        private static final long serialVersionUID = -7005294331015764472L;

        @ApiModelProperty(value = "key", example = "转入")
        private String title;

        @ApiModelProperty(value = "value", example = "今日15点前")
        private String text;
    }

    /**
     * 文案
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CopywritingVO implements Serializable {

        private static final long serialVersionUID = -8503906627701388337L;
        @ApiModelProperty("类型")
        private String type;
        @ApiModelProperty("标题")
        private String title;
        @ApiModelProperty("文本")
        private String text;
        @ApiModelProperty("子文案")
        private List<TradeRuleVO.CopywritingVO> contents;
    }
}
