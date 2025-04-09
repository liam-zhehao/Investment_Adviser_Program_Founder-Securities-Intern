package com.foundersc.ifte.invest.adviser.web.util;

import com.foundersc.itc.product.utils.DateUtils;

import java.util.Date;

public class TargetDateUtil {

    private final static String PADDING_DIGIT = "0";
    private static final int LOCALTIME_LEN = 6;
    public static final String PREDICT = "预计";
    public static final String REDEEM_ARRIVE_DESC = " 20:00前到账，以实际资金到账时间为准";

    public static Date assembleDate(Integer date, Integer time) {
        if (time == null || time.equals(0)) {
            return DateUtils.getDate(date);
        }
        String dateStr = date.toString();
        String timeStr = formatTime(time.toString());
        return DateUtils.parseDate(dateStr.concat(timeStr), DateUtils.YYYYMMDDHHMMSS);
    }

    public static String getFormatEntrustDate(String dateStr) {
        StringBuffer sb = new StringBuffer();
        if (dateStr != null) {
            if (dateStr.length() == 8) {
                sb.append(dateStr, 0, 4);
                sb.append("-");
                sb.append(dateStr, 4, 6);
                sb.append("-");
                sb.append(dateStr, 6, 8);
            } else {
                sb.append(dateStr);
            }
        } else {
            sb.append("-");
        }
        return sb.toString();
    }


    public static String formatTime(String timeStr) {
        while (timeStr.length() < LOCALTIME_LEN) {
            timeStr = PADDING_DIGIT + timeStr;
        }
        return timeStr;
    }


}
