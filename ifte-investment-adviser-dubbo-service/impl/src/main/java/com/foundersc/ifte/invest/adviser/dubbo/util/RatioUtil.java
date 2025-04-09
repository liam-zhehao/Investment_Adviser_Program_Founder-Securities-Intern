package com.foundersc.ifte.invest.adviser.dubbo.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @author wangfuwei
 * @date 2022/9/27
 */
public class RatioUtil {
    private RatioUtil() {
    }

    /**
     * 格式化百分比
     *
     * @param bigDecimal
     * @param fractionDigit
     * @return
     */
    public static String formatPercent(BigDecimal bigDecimal, int fractionDigit) {
        if (bigDecimal == null) {
            return StringUtils.EMPTY;
        }
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(fractionDigit);
        percentInstance.setMinimumFractionDigits(fractionDigit);
        percentInstance.setRoundingMode(RoundingMode.HALF_UP);
        return percentInstance.format(bigDecimal.doubleValue());
    }

    /**
     * 格式化百分比，默认百分比两位小数
     *
     * @param bigDecimal
     * @return
     */
    public static String formatPercent(BigDecimal bigDecimal) {
        return formatPercent(bigDecimal, 2);
    }
}
