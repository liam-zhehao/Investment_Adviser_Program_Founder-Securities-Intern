package com.foundersc.ifte.invest.adviser.dubbo.enums;

import lombok.Getter;

@Getter
public enum QueryTypeEnum {

    HIS_DATE("0", "按归历史日期查询"),
    NAV_DATE("1", "按净值日期查询");

    QueryTypeEnum(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    String id;

    String desc;
}
