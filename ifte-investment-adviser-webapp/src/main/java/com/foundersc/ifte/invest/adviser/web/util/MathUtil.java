package com.foundersc.ifte.invest.adviser.web.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by HuangZhifang on 2019/3/15
 */
@Slf4j
public class MathUtil {

    public final static double ZERO_DOUBLE = 0.0;
    public final static String FORMAT_PATTERN = "###,###.";


    /**
     * 四舍五入
     *
     * @param input
     * @param scale 保留几位小数
     * @return
     */
    public static double halfUp(double input, int scale) {
        BigDecimal bg = BigDecimal.valueOf(input);
        return bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 向下取整
     *
     * @param input
     * @param scale 保留几位小数
     * @return
     */
    public static double floor(double input, int scale) {
        BigDecimal bg = BigDecimal.valueOf(input);
        return bg.setScale(scale, BigDecimal.ROUND_FLOOR).doubleValue();
    }

    /**
     * string转double不损失精度
     *
     * @param str
     * @return
     */
    public static double stringToDouble(String str) {
        return new BigDecimal(str).doubleValue();
    }


    /**
     * 金额计数法 保留d位小数
     *
     * @param amount
     * @param d
     * @return
     */
    public static String formatAmount(Double amount, int d) {
        String prefix = "###0.";

        for (int i = 0; i < d; ++i) {
            prefix = prefix + "0";
        }

        String s = formatAmount(amount, prefix);
        if (s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
        }
        return delMinus(s, d);
    }

    /**
     * 保留d位小数 截断处理 为空时返回--
     *
     * @param amount
     * @return
     */
    public static String formatAmountByCut(Double amount, int d) {
        if (amount == null) {
            return "--";
        }
        return formatAmount(amount, d);
    }

    public static String formatAmount(Double amount, String template) {
        if (null == amount) {
            log.warn("amount is null!");
            amount = 0.0D;
        }

        DecimalFormat format = new DecimalFormat(template);
        return format.format(amount);
    }

    public static String formatSymbAmount(Double amount, int d) {
        if (null == amount) {
            return "--";
        }
        amount = halfUp(amount, 2);
        String amountStr = formatAmount(amount, d);
        if (amount != 0.0 && !"0.00".equals(amountStr)) {
            if (!amountStr.startsWith("-")) {
                return "+" + amountStr;
            }
        }
        return delMinus(amountStr, d);
    }

    public static String formatSymbAmount(Double amount, int scale, int d) {
        amount = halfUp(amount, scale);
        String zero = "0.";
        for (int i = 0; i < scale; ++i) {
            zero = zero + "0";
        }
        String amountStr = formatAmount(amount, d);
        if (amount != 0.0 && !zero.equals(amountStr)) {
            if (!amountStr.startsWith("-")) {
                return "+" + amountStr;
            }
        }
        return delMinus(amountStr, d);
    }


    public static String formatPercentByCut(Double rate, int d, boolean withPlusSign) {
        String s = BigDecimal.valueOf(rate).multiply(BigDecimal.valueOf(100.0D)).toPlainString();
        String[] arr = s.split("\\.");
        if (arr.length == 2 && arr[1].length() > d) {
            s = arr[0] + "." + arr[1].substring(0, d);
        }

        double tmp = Double.parseDouble(s);
        String amount = withPlusSign ? formatSymbAmount(tmp, d) : formatAmount(tmp, d);
        return amount + "%";
    }


//    public static String formatAmountToFloor(Double amount, int d, boolean plusSign) {
//        amount = floor(amount, 2);
//        String amountStr = formatAmount(amount, d);
//        String plusSignStr = plusSign ? "+" : "";
//        if (amount != 0.0 && !"0.00".equals(amountStr) && !amountStr.startsWith("-")) {
//            return plusSignStr + amountStr;
//        }
//        return delMinus(amountStr, d);
//    }
//
//    public static String formatFloorPercentage(Double amount, int d) {
//        return formatAmountToFloor(amount * 100, d, true) + "%";
//    }


    // 金额为0时去除负号
    private static String delMinus(String s, int d) {
        String minus = "-0.";
        for (int i = 0; i < d; i++) {
            minus = minus.concat("0");
        }
        if (s.equals(minus)) {
            return s.replaceAll("-", "");
        }
        return s;
    }

    /**
     * 将double类型转换为String(可转换单位，保留两位小数)
     *
     * @param number 转换数字
     * @param scale  保留小数位数
     * @param unit   单位
     */
    public static String formatNumToString(Double number, Integer scale, Double unit) {
        if (number == null || number.isNaN() || number.isInfinite()) {
            return "--";
        }
        number = number / unit;
        String sNum = String.valueOf(number);
        return new BigDecimal(sNum).setScale(scale, BigDecimal.ROUND_HALF_UP) + "";
    }

    public static Integer formatStringToInt(String num) {
        if (StringUtils.isNumeric(num)) {
            return Integer.valueOf(num);
        }
        return null;
    }

    /**
     * 格式化金额，千分位分割，正负号显示
     *
     * @param amount 金额
     * @param digits 小数位数
     * @param symb 是否加正号，0不加正负号
     * @return
     */
    public static String formatCommaStyleAmount(Double amount, int digits, boolean symb) {
        if (null == amount || digits
             <= 0) {
            return "--";
        }
        StringBuilder formatPattern = new StringBuilder(FORMAT_PATTERN);
        for (int i = 0; i < digits
            ; i++) {
            formatPattern.append("0");
        }
        if (symb && amount > 0) {
            formatPattern.insert(0, "+");
        }
        DecimalFormat decimalFormat = new DecimalFormat(formatPattern.toString());
        String amountStr = decimalFormat.format(amount);
        return processSpecial(amountStr);
    }

    private static String processSpecial(String amountStr) {
        if (StringUtils.isBlank(amountStr)) {
            return "--";
        }
        if (amountStr.startsWith("-.") || amountStr.startsWith("+.")|| amountStr.startsWith(".")) {
            amountStr = amountStr.replaceFirst("\\.", "0.");
        }
        String number = amountStr.replaceAll("[-.,+0]", "");
        if (StringUtils.isBlank(number)) {
            amountStr = amountStr.replaceFirst("-", "");
        }
        return amountStr;
    }


    public static void main(String[] args) {
        DecimalFormat df2 = new DecimalFormat("+###,###.0000");
        System.out.println(formatCommaStyleAmount(0.002,5,true));
    }
}
