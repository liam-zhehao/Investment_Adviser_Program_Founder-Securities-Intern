package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import lombok.Getter;

@Getter
public enum FundTransStatusEnum {

    PRE_TRANS("0", "未划拨"),
    FREEZING("a", "未解冻"),
    UNFREEZE_SUCCESS("b", "解冻成功"),
    PARTIAL_UNFREEZE_SUCCESS("c", "部分解冻成功"),
    UNFREEZE_FAIL("d", "解冻失败"),
    UNFREEZE_TIMEOUT("e", "解冻超时"),
    TRANS_SUCCESS("1", "划拨成功"),
    PARTIAL_TRANS_SUCCESS("2", "部分划拨成功"),
    TRANS_FAIL("3", "划拨失败"),
    UNFREEZE("4", "未冻结"),
    FREEZE_SUCCESS("5", "冻结成功"),
    PARTIAL_FREEZE_SUCCESS("6", "部分冻结成功"),
    FREEZE_FAIL("7", "冻结失败"),
    FREEZE_TIMEOUT("8", "冻结超时"),
    NO_NEED_TRANS("9", "无需划拨");

    FundTransStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
}
