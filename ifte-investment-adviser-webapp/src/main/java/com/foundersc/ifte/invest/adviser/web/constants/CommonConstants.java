package com.foundersc.ifte.invest.adviser.web.constants;

import java.math.BigDecimal;

/**
 * 通用常量
 */
public class CommonConstants {
    private CommonConstants() {
    }

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 保留天整数日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";

    /**
     * 年-月-日
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 年月
     */
    public static final String DEFAULT_DATE_MONTH_FORMAT = "yyyyMM";

    /**
     * 理财信息日期格式
     */
    public static final String FIN_INFO_DATE_FORMAT = "yyyy.MM.dd";

    /**
     * MM-dd
     */
    public static final String MD_DATE_TIME_FORMAT = "MM-dd";

    /**
     * 月-日
     */
    public static final String CH_MD_DATE_TIME_FORMAT = "MM月dd日";

    /**
     * 年-月-日
     */
    public static final String YMD_DATE_TIME_FORMAT = "yyyy-MM-dd";

    /**
     * 时分秒
     */
    public static final String LOCAL_TIME_FORMAT = "HHmmss";

    /**
     * 年月日时分秒毫秒微秒
     */
    public static final String LONG_TIME_FORMAT = "yyyyMMddHHmmssSSSSSS";

    /**
     * 客户号登录网关鉴权成功后，在HEADER中添加的鉴权结果
     */
    public static final String W_GATEWAY_CLIENT = "W-GATEWAY-CLIENT";

    /**
     * 手机号登录成功后的token
     */
    public static final String MOBILE_AUTH_TOKEN_KEY = "uToken";

    /**
     * 理财购买开始时间
     */
    public static final String PURCHASE_START_TIME = "09:14:59";

    /**
     * 理财购买结束时间
     */
    public static final String PURCHASE_END_TIME = "15:30:00";

    /**
     * 异常日志前缀
     */
    public static final String ALARM_PRE = "Error_";

    /**
     * kyc问卷答案格式
     */
    public static final String KYC_ANSWER_PATTERN = "%s#0&%s";

    /**
     * kyc问卷问题分隔符
     */
    public static final String KYC_QUESTION_SEPARATOR = "\\|";

    public static final String KYC_QUESTION_JOIN = "|";

    /**
     * kyc问卷答案问题分数分隔符
     */
    public static final String KYC_QUESTION_N0_SCORE_SEPARATOR = "#";

    /**
     * kyc问卷答案分数答案分隔符
     */
    public static final String KYC_SCORE_ANSWER_NO_SEPARATOR = "&";

    /**
     * kyc问卷问题连接符
     */
    public static final String KYC_ANSWER_JOINER = "^";

    /**
     * kyc问卷问题分隔符
     */
    public static final String KYC_ANSWER_SEPARATOR = "\\^";

    /**
     * 用户投资需求答案分隔符
     */
    public static final String INVESTDEMAND_ANSWER_SEPARATOR = "、";

    /**
     * 组合推荐列表查询 标签类型 kyc标签
     */
    public static final String RECOMMEND_COMB_ORDER_DIRECTION = "desc";

    /**
     * 组合推荐列表查询 标签类型 kyc标签
     */
    public static final String EN_TAG_TYPE = "0";

    public static final String BLANK_STR = " ";

    /**
     * 客户号登录token
     */
    public static final String CLIENT_AUTH_TOKEN_KEY = "token";

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    public static final String CHAR_PERCENT = "%";

    public static final String CHINESE_COMMA = "，";

    public static final String USER_AGENT = "User-Agent";

    public static final String IOS = "ios";

    public static final String ANDROID = "android";

    public static final String DEVICE_ID = "DeviceID";

    public static final String OP_STATION = "Op-Station";

    public static final int TIME_LENGTH_SHORT = 5;

    public static final int TIME_LENGTH_LONG = 6;

    public static final int DECIMAL_PRECISION = 2;

    public static final String ZERO = "0";

    public static final Integer ZERO_INT = 0;

    public static final String PERIOD_CONN = "~";

    public static final String DAY = "天";

    public static final String COMMA = "，";

    public static final String PERIOD = "。";


    public static final String JOIN_SPLIT = "|";

    public final static int CHECK_TIME = 120000;

    public final static String MBYKYC = "MBYKYC-";

    public final static String MBYKYC_SPLIT = "-";

    // 不支持目标盈的版本号信息
    public final static String TARGET_VERSION = "8.28.99";


}
