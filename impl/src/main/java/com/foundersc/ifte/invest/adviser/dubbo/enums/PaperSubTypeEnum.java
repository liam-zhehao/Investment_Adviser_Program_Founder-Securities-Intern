package com.foundersc.ifte.invest.adviser.dubbo.enums;

import lombok.Getter;

@Getter
public enum PaperSubTypeEnum {
    TARGET_PAPER("1", "目标盈"),
    COMMON_PAPER("!", "普通投顾");


    private String typeCode;

    private String desc;

    PaperSubTypeEnum(String typeCode, String desc) {
        this.typeCode = typeCode;
        this.desc = desc;
    }

}
