package com.foundersc.ifte.invest.adviser.web.util;

import com.foundersc.itc.product.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DataDealUtil {
    public static final Pattern ERROR_MSG_PATTERN = Pattern.compile("\\[\\d+]\\[(.*?)].*");

    private DataDealUtil() {

    }

    public static String filterErrorMsg(String original) {
        if (StringUtils.isEmpty(original)) {
            return "参数错误";
        }
        try {
            Matcher matcher = ERROR_MSG_PATTERN.matcher(original);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception ignored) {
        }
        return original;
    }

    public static String getDesignateDate(Date originDate, Integer offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originDate);
        calendar.add(Calendar.DAY_OF_YEAR, offset);
        return DateUtils.getIntegerDate(calendar.getTime()).toString();
    }
}
