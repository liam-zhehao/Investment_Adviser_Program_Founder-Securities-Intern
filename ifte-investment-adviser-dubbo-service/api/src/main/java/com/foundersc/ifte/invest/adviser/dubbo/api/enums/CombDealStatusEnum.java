package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public enum CombDealStatusEnum {

    UN_START("0", "未运作", "未开放", "1"),
    PURCHASE("1", "开放购买期", "开放中", "1"),
    UN_OPERATE_CALM("2", "未运作冷静期", "运作中", "1"),
    OPERATE_CALM("3", "运作冷静期", "运作中", "2"),
    STOP_PROFIT_MONITOR("4", "运作止盈监控期", "运作中", "2"),
    PROFIT_STOPPING("5", "止盈中", "运作中", "2"),
    PROFIT_STOPPED("6", "已止盈", "已止盈", "2"),
    TERMINATED("7", "已到期", "已到期", "2"),
    STOP_PROFIT_RENEWING("8", "止盈待续存", "已止盈", "2");

    private String code;

    private String desc;

    private String display;

    private String calculatorKey;

    CombDealStatusEnum(String code, String desc, String display, String calculatorKey) {
        this.code = code;
        this.desc = desc;
        this.display = display;
        this.calculatorKey = calculatorKey;
    }

    /**
     * 终止状态
     */
    public static List<String> stoppedStatus = Lists.newArrayList(PROFIT_STOPPED.getCode(), TERMINATED.getCode(), STOP_PROFIT_RENEWING.getCode());

    /**
     * 运作中状态
     */

    public static List<String> operatingStatus = Lists.newArrayList(OPERATE_CALM.getCode(), STOP_PROFIT_MONITOR.getCode(), PROFIT_STOPPING.getCode());


    /**
     * 所有状态
     */
    public static List<String> allStatus = Lists.newArrayList(UN_START.getCode(), PURCHASE.getCode(), UN_OPERATE_CALM.getCode(),
            OPERATE_CALM.getCode(), STOP_PROFIT_MONITOR.getCode(), PROFIT_STOPPED.getCode(), PROFIT_STOPPING.getCode(), TERMINATED.getCode(), STOP_PROFIT_RENEWING.getCode());


    public static CombDealStatusEnum parseByStatusCode(String statusCode) {
        for (CombDealStatusEnum value : CombDealStatusEnum.values()) {
            if (value.getCode().equals(statusCode)) {
                return value;
            }
        }
        return null;
    }
}
