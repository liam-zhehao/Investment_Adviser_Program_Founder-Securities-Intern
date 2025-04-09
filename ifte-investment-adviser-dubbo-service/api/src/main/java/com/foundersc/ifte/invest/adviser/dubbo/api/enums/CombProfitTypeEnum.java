package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public enum CombProfitTypeEnum {

    COMMON_COMB("0", "普通组合"),
    TARGET_COMB("1", "目标盈组合"),
    HOLD_COMB("2", "持有期组合"),
    MONEY_COMB("3", "货币组合宝");

    private String typeId;

    private String typeName;

    CombProfitTypeEnum(String typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public static List<String> allCombTypes = Lists.newArrayList(COMMON_COMB.getTypeId(), TARGET_COMB.getTypeId(), HOLD_COMB.getTypeId(), MONEY_COMB.getTypeId());

    public static CombProfitTypeEnum parseByTypeId(String typeId) {
        for (CombProfitTypeEnum value : CombProfitTypeEnum.values()) {
            if (value.getTypeId().equals(typeId)) {
                return value;
            }
        }
        return null;
    }
}
