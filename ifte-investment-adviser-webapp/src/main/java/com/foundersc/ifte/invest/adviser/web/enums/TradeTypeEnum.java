package com.foundersc.ifte.invest.adviser.web.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public enum TradeTypeEnum {

    PURCHASE(1, "转入"),
    REDEEM(2, "转出");

    private Integer typeCode;

    private String typeName;

    TradeTypeEnum(Integer typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public static List<Integer> allTypeCodes = Lists.newArrayList(PURCHASE.getTypeCode(), REDEEM.getTypeCode());

}
