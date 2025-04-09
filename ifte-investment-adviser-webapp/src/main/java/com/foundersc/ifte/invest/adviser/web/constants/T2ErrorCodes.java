package com.foundersc.ifte.invest.adviser.web.constants;

/**
 * @author wangfuwei
 * @date 2023/10/23
 */
public class T2ErrorCodes {
    private T2ErrorCodes(){}

    /**
     * 组合申请不允许撤单错误码
     */
    public static final Integer NOT_ALLOW_RECALL = 39112;

    /**
     * 减少投资时，存在在途委托不允许赎回错误码
     */
    public static final Integer NOT_ALLOW_REDUCE_39049 = 39049;

    /**
     * 解约时，存在在途委托不允许解约错误码
     */
    public static final Integer NOT_ALLOW_REDUCE_39234 = 39234;
}
