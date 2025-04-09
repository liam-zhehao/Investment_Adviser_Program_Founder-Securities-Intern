package com.foundersc.ifte.invest.adviser.web.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public enum ListTypeEnum {

    CURR_LIST(0, "在售列表"),
    HIS_LIST(1, "历史列表");

    private Integer typeId;

    private String typeDesc;

    ListTypeEnum(Integer typeId, String typeDesc) {
        this.typeId = typeId;
        this.typeDesc = typeDesc;
    }

    public static List<Integer> allListType = Lists.newArrayList(CURR_LIST.getTypeId(), HIS_LIST.getTypeId());
}
