package com.foundersc.ifte.invest.adviser.web.enums;

import lombok.Getter;

@Getter
public enum CalculatorTypeEnum {
    RANGE_CAL("1", "运作区间计算"),
    OPERATION_DAY_CAL("2", "实际运作天数计算");

    private String typeId;

    private String desc;

    CalculatorTypeEnum(String typeId, String desc) {
        this.typeId = typeId;
        this.desc = desc;
    }
}
