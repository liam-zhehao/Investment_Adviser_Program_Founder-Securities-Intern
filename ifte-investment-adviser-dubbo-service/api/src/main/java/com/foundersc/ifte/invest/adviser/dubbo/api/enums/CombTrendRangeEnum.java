package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import com.foundersc.ifc.common.util.DateUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wangfuwei
 * @date 2022/9/26
 */
@Getter
@Slf4j
public enum CombTrendRangeEnum {
    ONE_MONTH(1, "近1月", 1, 0, 30, 0),
    THREE_MONTH(2, "近3月", 2, 30, 90, 0),
    SIX_MONTH(3, "近6月", 3, 90, 180, 0),
    ONE_YEAR(4, "近1年", 4, 180, 365, 1),
    SINCE_BEGIN(5, "成立以来", 5, 0, Integer.MAX_VALUE, 1),
    // 本期没有今年以来，先将displayPos置为-1
    SINCE_CURRENT_YEAR(6, "今年以来", 6, 0, Integer.MAX_VALUE, -1),
//    FIVE_YEAR(9, "近5年", 9, 365, 365 * 5, 2),
    ;

    /**
     * 区间id
     */
    private Integer rangeId;

    /**
     * 区间描述
     */
    private String rangeDesc;

    /**
     * 展示顺序
     */
    private Integer order;

    /**
     * 要展示时成立的最小天数
     */
    private Integer minDays;

    /**
     * 区间天数
     */
    private Integer rangeDays;

    /**
     * 展示位置：0 组合详情和持仓详情都展示；1 组合详情；2 持仓详情
     */
    private Integer displayPos;

    private final static Date defaultSetupDate = DateUtils.parse("20150101", "yyyyMMdd");

    CombTrendRangeEnum(Integer rangeId, String rangeDesc, Integer order, Integer minDays, Integer rangeDays, Integer displayPos) {
        this.rangeId = rangeId;
        this.rangeDesc = rangeDesc;
        this.order = order;
        this.minDays = minDays;
        this.rangeDays = rangeDays;
        this.displayPos = displayPos;
    }

    /**
     * 根据组合成立日期，计算要展示的区间
     *
     * @param combSetupDays 组合成立天数（用当前日期的上一个交易日-成立日期+1）
     * @return
     */
    public static List<CombTrendRangeEnum> getCombTrendEnableRanges(int combSetupDays, int displayPos) {
        if (combSetupDays == 0) {
            return new ArrayList<>();
        }
        List<CombTrendRangeEnum> trendRanges = new ArrayList<>();
        for (CombTrendRangeEnum value : getCombTrendRanges(displayPos)) {
            if (combSetupDays > value.getMinDays()) {
                trendRanges.add(value);
            }
        }
        return trendRanges.stream()
                .sorted(Comparator.comparing(CombTrendRangeEnum::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 要展示的区间
     *
     * @param displayPos 要展示的位置 1 组合详情；2 持仓详情
     * @return
     */
    public static List<CombTrendRangeEnum> getCombTrendRanges(int displayPos) {
        List<CombTrendRangeEnum> trendRanges = new ArrayList<>();
        // 展示displayPos为0和指定参数所有的
        for (CombTrendRangeEnum value : CombTrendRangeEnum.values()) {
            if (value.getDisplayPos() == 0 || value.getDisplayPos() == displayPos) {
                trendRanges.add(value);
            }
        }
        return trendRanges.stream()
                .sorted(Comparator.comparing(CombTrendRangeEnum::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 获取展示区间
     *
     * @param combSetupDate 组合成立日期
     * @param lastTradeDate 用当前日期的上一个交易日
     * @return
     */
    public Date[] getShowRange(Date combSetupDate, Date lastTradeDate) {
        if (SINCE_CURRENT_YEAR.equals(this)) {
            // 今年以来需要特殊处理
            LocalDate localDate = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
            return new Date[]{localDateToDate(localDate), lastTradeDate};
        }
        if (combSetupDate == null) {
            log.warn("combSetupDate is null, use default={}", defaultSetupDate);
            combSetupDate = defaultSetupDate;
        }
        int diffDays = DateUtils.getDateDiffDays(combSetupDate, lastTradeDate);
        if (diffDays < rangeDays) {
            return new Date[]{combSetupDate, lastTradeDate};
        } else {
            return new Date[]{Date.from(lastTradeDate.toInstant().minus(Duration.ofDays(rangeDays))), lastTradeDate};
        }
    }

    public static CombTrendRangeEnum parseByRangeId(Integer rangeId) {
        for (CombTrendRangeEnum value : CombTrendRangeEnum.values()) {
            if (value.getRangeId().equals(rangeId)) {
                return value;
            }
        }
        return null;
    }

    /**
     * LocalDate转Date
     *
     * @param localDate
     * @return
     */
    private Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
