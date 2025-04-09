package com.foundersc.ifte.invest.adviser.web.util;

import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author wangfuwei
 * @date 2022/10/18
 */
@Slf4j
public class DataUtil {
    private DataUtil() {
    }

    /**
     * 字符串转为BigDecimal，转换失败默认返回null
     *
     * @param str
     * @return
     */
    public static BigDecimal strToBigDecimal(String str) {
        return strToBigDecimal(str, null);
    }

    /**
     * 字符串转为BigDecimal，转换失败默认返回0
     *
     * @param str
     * @return
     */
    public static BigDecimal strToBigDecimalDefaultZero(String str) {
        return strToBigDecimal(str, BigDecimal.ZERO);
    }

    /**
     * 字符串转为BigDecimal
     *
     * @param str
     * @param defaultValue
     * @return
     */
    public static BigDecimal strToBigDecimal(String str, BigDecimal defaultValue) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            return new BigDecimal(str);
        } catch (Exception e) {
            log.warn("strToBigDecimal error, str={}, use defaultValue={}", str, e, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 字符串转int
     *
     * @param str
     * @return
     */
    public static int strToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            log.error("strToInt error, str={}", str, e);
            return 0;
        }
    }

    /**
     * 是否等于1
     *
     * @param bigDecimal
     * @return
     */
    public static boolean isOne(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return false;
        }
        return BigDecimal.ONE.compareTo(bigDecimal) == 0;
    }

    /**
     * 是否等于0
     *
     * @param bigDecimal
     * @return
     */
    public static boolean isZero(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return false;
        }
        return BigDecimal.ZERO.compareTo(bigDecimal) == 0;
    }

    /**
     * 是否大于0
     *
     * @param bigDecimal
     * @return
     */
    public static boolean isPositive(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return false;
        }
        return BigDecimal.ZERO.compareTo(bigDecimal) < 0;
    }

    /**
     * 是否小于0
     *
     * @param bigDecimal
     * @return
     */
    public static boolean isNegative(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return false;
        }
        return BigDecimal.ZERO.compareTo(bigDecimal) > 0;
    }

    /**
     * 获取最大的数
     *
     * @param b1
     * @param b2
     * @return
     */
    public static BigDecimal getMax(BigDecimal b1, BigDecimal b2) {
        if (b1 == null || b2 == null) {
            throw new BizException(BizErrorCodeEnum.PARAM_ERROR);
        }
        if (b1.compareTo(b2) > 0) {
            return b1;
        }
        return b2;
    }

    /**
     * 是否有小数
     *
     * @param b
     * @return
     */
    public static boolean hasDot(BigDecimal b) {
        return getDotNum(b) != 0;
    }

    /**
     * 小数点位数
     *
     * @param b
     * @param max 最大位数（包含）
     * @return
     */
    public static int getDotNum(BigDecimal b, int max) {
        int result = 0;
        if (b == null) {
            return result;
        }
        while (new BigDecimal(b.intValue()).compareTo(b) != 0) {
            result++;
            b = b.multiply(new BigDecimal(10));
            if (result > max) {
                log.warn("dot num large than max={}", max);
                result--;
                break;
            }
        }
        return result;
    }

    /**
     * 小数点位数
     *
     * @param b
     * @return
     */
    public static int getDotNum(BigDecimal b) {
        return getDotNum(b, Integer.MAX_VALUE);
    }
}
