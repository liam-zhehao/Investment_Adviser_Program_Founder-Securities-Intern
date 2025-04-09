package com.foundersc.ifte.invest.adviser.dubbo.util;

import com.foundersc.ifc.common.util.DateUtils;
import com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author wangfuwei
 * @date 2022/9/26
 */
@Slf4j
public class DateUtil {
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
        return DateUtils.formatDate(date, CommonConstants.MD_DATE_TIME_FORMAT);
    }

    /**
     * LocalDate转Date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * int转yyyy-MM-dd
     *
     * @param intDate
     * @return
     */
    public static String intToFormatDate(Integer intDate) {
        if (intDate == null) {
            return StringUtils.EMPTY;
        }
        Date date = intToDate(intDate);
        return DateUtils.formatDate(date, CommonConstants.DATE_FORMAT);
    }

    /**
     * 去年今日
     *
     * @return
     */
    public static Date lastYearDate() {
        LocalDate now = LocalDate.now();
        LocalDate lastYearLocalDate = now.plusYears(-1);
        return localDateToDate(lastYearLocalDate);
    }

    /**
     * 去年今日
     *
     * @return
     */
    public static LocalDate lastYearLocalDate() {
        LocalDate now = LocalDate.now();
        return now.plusYears(-1);
    }

    /**
     * int转localDate
     *
     * @param intDate
     * @return
     */
    public static LocalDate intToLocalDate(Integer intDate) {
        Date date = intToDate(intDate);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 当前日期
     *
     * @return
     */
    public static LocalDate localDate() {
        return new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * int转localTime
     *
     * @param intTime
     * @return
     */
    public static LocalTime intToLocalTime(Integer intTime) {
        try {
            if (intTime == null) {
                return LocalTime.MIN;
            }
            if (intTime == 0) {
                return LocalTime.MIN;
            }
            String strTime = intTime.toString();
            if (strTime.length() == 3 || strTime.length() == 4) {
                // 例如：01时01分，10时01分
                return LocalTime.of(intTime / 100, intTime % 100);
            }
            if (strTime.length() == 5 || strTime.length() == 6) {
                // 例如：01时01分01秒，10时01分01秒
                return LocalTime.of(intTime / (100 * 100), (intTime / 100) % 100, intTime % 100);
            }
            return LocalTime.MIN;
        } catch (Exception e) {
            log.warn("intToLocalTime error, intTime={}", intTime, e);
            return LocalTime.MIN;
        }
    }

    /**
     * 转localDateTime
     *
     * @param intDate
     * @param intTime
     * @return
     */
    public static LocalDateTime toLocalDateTime(Integer intDate, Integer intTime) {
        LocalDate localDate;
        if (intDate == null || intDate.toString().length() < 6) {
            localDate = LocalDate.MIN;
        } else {
            localDate = intToLocalDate(intDate);
        }
        return LocalDateTime.of(localDate, intToLocalTime(intTime));
    }

    /**
     * 当前是否介于指定时间区间
     *
     * @param startDate
     * @param startTime
     * @param endDate
     * @param endTime
     * @return
     */
    public static boolean isBetween(Integer startDate, Integer startTime, Integer endDate, Integer endTime) {
        return isAfter(startDate, startTime) && isBefore(endDate, endTime);
    }

    /**
     * 当前是否在指定时间之后
     *
     * @param startDate
     * @param startTime
     * @return
     */
    public static boolean isAfter(Integer startDate, Integer startTime) {
        return LocalDateTime.now().isAfter(toLocalDateTime(startDate, startTime));
    }

    /**
     * 当前是否在指定时间之后
     *
     * @param endDate
     * @param endTime
     * @return
     */
    public static boolean isBefore(Integer endDate, Integer endTime) {
        return LocalDateTime.now().isBefore(toLocalDateTime(endDate, endTime));
    }
}
