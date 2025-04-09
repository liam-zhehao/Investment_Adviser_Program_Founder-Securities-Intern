package com.foundersc.ifte.invest.adviser.dubbo.enums;

import lombok.Getter;

@Getter
public enum SortEnum {

    DESC(0, "按日期降序"),
    ASC(1, "按日期升序");

    SortEnum(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    int id;
    String desc;
}
