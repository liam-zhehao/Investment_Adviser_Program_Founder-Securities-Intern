package com.foundersc.ifte.invest.adviser.dubbo.service.impl.support;

import com.foundersc.ifte.invest.adviser.dubbo.util.DataTypeUtil;
import com.foundersc.ifte.invest.adviser.dubbo.util.RatioUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算收益率
 *
 * @author wangfuwei
 * @date 2022/9/26
 */
@Slf4j
public class IncomeRatioCalculator {
    /**
     * 正数前缀
     */
    private final static String POSITIVE_PREFIX = "+";

    /**
     * 根据累计净值计算收益率：
     * (区间内最后一天的累计净值-区间内第一天的累计净值)/区间内第一天的累计净值
     *
     * @param strStartSumNetValue 区间内第一天的累计净值
     * @param strEndSumNetValue   区间内最后一天的累计净值
     * @return
     */
    public static BigDecimal calcIncomeRatioByStrSumNetValue(String strStartSumNetValue, String strEndSumNetValue) {
        BigDecimal startSumNetValue = DataTypeUtil.strToBigDecimal(strStartSumNetValue);
        BigDecimal endSumNetValue = DataTypeUtil.strToBigDecimal(strEndSumNetValue);
        return calcIncomeRatioBySumNetValue(startSumNetValue, endSumNetValue);
    }

    /**
     * 根据累计收益率计算区间收益率：
     * 先根据累计收益率计算累计净值，然后根据累计净值计算区间收益率
     * (区间内最后一天的累计净值-区间内第一天的累计净值)/区间内第一天的累计净值
     *
     * @param strStartSumIncomeRatio
     * @param strEndSumIncomeRatio
     * @return
     */
    public static BigDecimal calcIncomeRatioByStrSumIncomeRatio(String strStartSumIncomeRatio, String strEndSumIncomeRatio) {
        BigDecimal startSumIncomeRatio = DataTypeUtil.strToBigDecimal(strStartSumIncomeRatio);
        BigDecimal endSumIncomeRatio = DataTypeUtil.strToBigDecimal(strEndSumIncomeRatio);
        return calcIncomeRatioBySumIncomeRatio(startSumIncomeRatio, endSumIncomeRatio);
    }

    /**
     * 根据累计净值计算收益率：
     * (区间内最后一天的累计净值-区间内第一天的累计净值)/区间内第一天的累计净值
     *
     * @param startSumNetValue 区间内第一天的累计净值
     * @param endSumNetValue   区间内最后一天的累计净值
     * @return
     */
    public static BigDecimal calcIncomeRatioBySumNetValue(BigDecimal startSumNetValue, BigDecimal endSumNetValue) {
        if (startSumNetValue == null || endSumNetValue == null) {
            return BigDecimal.ZERO;
        }
        if (startSumNetValue.equals(BigDecimal.ZERO)) {
            if (endSumNetValue.equals(startSumNetValue)) {
                return BigDecimal.ZERO;
            } else {
                return BigDecimal.ONE;
            }
        }
        BigDecimal incomeRatio = endSumNetValue.subtract(startSumNetValue).divide(startSumNetValue, 4, RoundingMode.HALF_UP);
        incomeRatio = incomeRatio.setScale(4, BigDecimal.ROUND_HALF_UP);
        return incomeRatio;
    }

    /**
     * 根据累计收益率计算区间收益率：
     * 先根据累计收益率计算累计净值，然后根据累计净值计算区间收益率
     * (区间内最后一天的累计净值-区间内第一天的累计净值)/区间内第一天的累计净值
     *
     * @param startSumIncomeRatio 区间内第一天的累计收益率
     * @param endSumIncomeRatio   区间内最后一天的累计收益率
     * @return
     */
    public static BigDecimal calcIncomeRatioBySumIncomeRatio(BigDecimal startSumIncomeRatio, BigDecimal endSumIncomeRatio) {
        BigDecimal startSumNetValue = calcSumNetValueBySumIncomeRatio(startSumIncomeRatio);
        BigDecimal endSumNetValue = calcSumNetValueBySumIncomeRatio(endSumIncomeRatio);
        return calcIncomeRatioBySumNetValue(startSumNetValue, endSumNetValue);
    }

    /**
     * 根据累计收益率计算累计净值：
     * 累计净值=1+累计收益率
     *
     * @param sumIncomeRatio
     * @return
     */
    public static BigDecimal calcSumNetValueBySumIncomeRatio(BigDecimal sumIncomeRatio) {
        if (sumIncomeRatio == null) {
            log.warn("sumIncomeRatio is null, use default value zero");
            sumIncomeRatio = BigDecimal.ZERO;
        }
        return BigDecimal.ONE.add(sumIncomeRatio);
    }

    /**
     * 格式化
     *
     * @param incomeRatio
     * @return
     */
    public static String formatCalIncomeRatio(BigDecimal incomeRatio) {
        if (incomeRatio == null) {
            return StringUtils.EMPTY;
        }
        String result = RatioUtil.formatPercent(incomeRatio, 2);
        if (incomeRatio.compareTo(BigDecimal.ZERO) > 0) {
            return POSITIVE_PREFIX + result;
        }
        return result;
    }
}
