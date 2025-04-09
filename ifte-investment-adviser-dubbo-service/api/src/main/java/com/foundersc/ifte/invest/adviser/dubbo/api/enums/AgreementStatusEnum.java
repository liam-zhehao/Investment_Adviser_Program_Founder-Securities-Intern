package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import lombok.Getter;

@Getter
public enum AgreementStatusEnum {
    PRE_EFFECTIVE(0, "未生效"),
    EFFECTIVE(1, "已生效"),
    POST_EFFECTIVE(2, "已失效"),
    LOSE_VALIDITY(3, "已作废"),
    EXPIRED(4, "已过期"),
    PRE_VISIT(5, "待回访"),
    CANCELING(6, "解约中"),
    SUSPEND(7, "暂停");

    AgreementStatusEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    int id;
    String desc;
}
