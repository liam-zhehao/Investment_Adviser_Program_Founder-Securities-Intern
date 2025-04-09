package com.foundersc.ifte.invest.adviser.dubbo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author wangfuwei
 * @date 2022/10/18
 */
@Slf4j
public class DataTypeUtil {
    private DataTypeUtil() {
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
}
