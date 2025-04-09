package com.foundersc.ifte.invest.adviser.web.enums;

import lombok.Getter;

/**
 * 组合申请类型
 *
 * @author wangfuwei
 * @date 2022/11/1
 */
@Getter
public enum CombRequestEnum {
    SIGN("1", "签约"),
    ADD_INVEST("2", "追加投资"),
    REDUCE_INVEST("3", "减少投资"),
    CANCEL("4", "解约"),
    REVOKE("5", "撤单"),
    PRE_ADD_INVEST("6", "预约追加投资");

    private String code;
    private String desc;

    CombRequestEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
