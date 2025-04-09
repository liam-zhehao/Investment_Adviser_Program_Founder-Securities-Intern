package com.foundersc.ifte.invest.adviser.web.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.CHAR_PERCENT;

/**
 * @author wangfuwei
 * @date 2022/9/27
 */
public class RatioUtil {
    private RatioUtil() {
    }

    /**
     * 正数前缀
     */
    private final static String POSITIVE_PREFIX = "+";

    /**
     * 格式化百分比
     *
     * @param bigDecimal
     * @param fractionDigit
     * @return
     */
    public static String formatPercent(BigDecimal bigDecimal, int fractionDigit, RoundingMode roundingMode) {
        if (bigDecimal == null) {
            return StringUtils.EMPTY;
        }
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        percentInstance.setMaximumFractionDigits(fractionDigit);
        percentInstance.setMinimumFractionDigits(fractionDigit);
        if (roundingMode != null) {
            percentInstance.setRoundingMode(roundingMode);
        }
        return percentInstance.format(bigDecimal);
    }

    /**
     * 格式化百分比
     *
     * @param bigDecimal
     * @param fractionDigit
     * @return
     */
    public static String formatPercent(BigDecimal bigDecimal, int fractionDigit) {
        return formatPercent(bigDecimal, fractionDigit, RoundingMode.HALF_UP);
    }

    /**
     * 格式化快捷赎回比例
     *
     * @param fastRatioValue
     * @return
     */
    @Deprecated
    public static String formatFastRatioValue(BigDecimal fastRatioValue) {
        if (fastRatioValue == null) {
            return StringUtils.EMPTY;
        }
        return fastRatioValue.intValue() + CHAR_PERCENT;
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

    /**
     * 格式化
     *
     * @param bigDecimal
     * @return
     */
    public static String formatPercentWithPrefix(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return StringUtils.EMPTY;
        }
        String result = RatioUtil.formatPercent(bigDecimal, 2);
        if (DataUtil.isPositive(bigDecimal)) {
            return POSITIVE_PREFIX + result;
        }
        return result;
    }
}
