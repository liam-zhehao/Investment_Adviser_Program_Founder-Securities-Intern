package com.foundersc.ifte.invest.adviser.web.util;

import com.foundersc.ifc.common.util.DateUtils;
import com.foundersc.ifte.invest.adviser.web.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.ZERO_INT;

/**
 * @author wangfuwei
 * @date 2022/9/28
 */
@Slf4j
public class DateUtil {
    private final static String PADDING_DIGIT = "0";
    private static final int LOCALTIME_LEN = 6;

    /**
     * date转为int
     *
     * @param date
     * @return
     */
    public static int dateToInt(Date date) {
        return Integer.parseInt(DateUtils.formatDate(date, CommonConstants.DEFAULT_DATE_FORMAT));
    }

    /**
     * date转yyyyMM
     *
     * @param date
     * @return
     */
    public static int dateToIntMonth(Date date) {
        return Integer.parseInt(DateUtils.formatDate(date, CommonConstants.DEFAULT_DATE_MONTH_FORMAT));
    }

    /**
     * LocalDateTime转int
     *
     * @param localDateTime
     * @return
     */
    public static int localDateTimeToInt(LocalDateTime localDateTime) {
        return DateUtil.dateToInt(Date.from(localDateTime.atZone(ZoneId.systemDefault())
                .toInstant()));
    }

    /**
     * int转LocalDateTime
     *
     * @param intDate
     * @return
     */
    public static LocalDateTime intToLocalDateTime(Integer intDate) {
        if (intDate == null) {
            return null;
        }
        Date date = DateUtil.intToDate(intDate);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDate转int
     *
     * @param localDate
     * @return
     */
    public static int localDateToInt(LocalDate localDate) {
        return DateUtil.dateToInt(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    /**
     * int转date
     *
     * @param intDate
     * @return
     */
    public static Date intToDate(Integer intDate) {
        if (intDate == null) {
            return null;
        }
        return DateUtils.parse(intDate.toString(), CommonConstants.DEFAULT_DATE_FORMAT);
    }

    /**
     * int转MM-dd
     *
     * @param intDate
     * @return
     */
    public static String formatMonthDay(int intDate) {
        Date date = intToDate(intDate);
        return formatMonthDay(date);
    }

    /**
     * int转MM月dd日
     *
     * @param intDate
     * @return
     */
    public static String formatChineseMonthDay(int intDate) {
        Date date = intToDate(intDate);
        return formatChineseMonthDay(date);
    }

    /**
     * int转yyyy-MM-dd
     *
     * @param intDate
     * @return
     */
    public static String formatYearMonthDay(int intDate) {
        Date date = intToDate(intDate);
        return DateUtils.formatDate(date, CommonConstants.YMD_DATE_TIME_FORMAT);
    }

    /**
     * date转MM-dd
     *
     * @param date
     * @return
     */
    public static String formatMonthDay(Date date) {
        return DateUtils.formatDate(date, CommonConstants.MD_DATE_TIME_FORMAT);
    }

    /**
     * date转MM月dd日
     *
     * @param date
     * @return
     */
    public static String formatChineseMonthDay(Date date) {
        return DateUtils.formatDate(date, CommonConstants.CH_MD_DATE_TIME_FORMAT);
    }

    /**
     * int赚localTime，int格式为HHmmss
     *
     * @param time
     * @return
     */
    public static LocalTime intToLocalTime(int time) {
        String strTime = String.valueOf(time);
        while (strTime.length() < LOCALTIME_LEN) {
            strTime = PADDING_DIGIT + strTime;
        }
        return LocalTime.parse(strTime, DateTimeFormatter.ofPattern(CommonConstants.LOCAL_TIME_FORMAT));
    }

    /**
     * 星期
     *
     * @param date
     * @return
     */
    public static String toChineseDate(Date date) {
        return cn.hutool.core.date.DateUtil.dayOfWeekEnum(date).toChinese();
    }

    /**
     * 星期
     *
     * @param intDate
     * @return
     */
    public static String toChineseDate(int intDate) {
        return toChineseDate(intToDate(intDate));
    }

    /**
     * 今天
     *
     * @return
     */
    public static int intDate() {
        return DateUtil.dateToInt(new Date());
    }

    /**
     * 去年今日
     *
     * @return
     */
    public static int lastYearIntDate() {
        LocalDate now = LocalDate.now();
        LocalDate lastYearLocalDate = now.plusYears(-1);
        return localDateToInt(lastYearLocalDate);
    }

    public static Integer diffDateBetween(Integer startDate, Integer endDate) {
        Long diffDays = cn.hutool.core.date.DateUtil.betweenDay(intToDate(startDate), intToDate(endDate), Boolean.TRUE);
        return diffDays.intValue();
    }

    public static String formatDate(Integer date) {
        if (date != null && !ZERO_INT.equals(date)) {
            return DateUtils.formatDate(com.foundersc.itc.product.utils.DateUtils.getDate(date), com.foundersc.itc.product.utils.DateUtils.YYYY_MM_DD);
        }
        return null;
    }

    /**
     * yyyy-MM-dd转intDate
     *
     * @param strDate
     * @return
     */
    public static int strToIntDate(String strDate) {
        try {
            return DateUtil.dateToInt(DateUtils.parse(strDate, CommonConstants.YYYY_MM_DD));
        } catch (Exception e) {
            log.warn("strToIntDate fail, strDate={}", strDate);
            return 0;
        }
    }

    /**
     * 是否是有效日期
     *
     * @param strDate
     * @return
     */
    public static boolean isValidDate(String strDate) {
        return StringUtils.isNotBlank(strDate) && !"0".equals(strDate);
    }
}
