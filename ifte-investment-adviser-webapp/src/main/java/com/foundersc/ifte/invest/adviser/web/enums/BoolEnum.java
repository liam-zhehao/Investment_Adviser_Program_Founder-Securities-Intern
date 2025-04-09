package com.foundersc.ifte.invest.adviser.web.enums;

import lombok.Getter;

@Getter
public enum BoolEnum {
    YES(1, "是"),
    NO(0, "否");

    private Integer id;

    private String desc;

    BoolEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }
}
