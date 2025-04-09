package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

/**
 * 组合申请业务类型
 */
@Getter
public enum CombBusTypeEnum {

    COMB_SIGN("1", "组合签约"),
    COMB_BUY("2", "追加投资"),
    COMB_REDEEM("3", "减少投资"),
    COMB_ADJUEST("4", "组合调仓"),
    COMB_CANCEL("6", "组合撤单"),
    COMB_TERMINATE("7", "组合解约"),
    COMB_APPLY_BUY("H", "组合预约购买")
    ;

    private String code;

    private String desc;

    CombBusTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<String> tradeRecordBusTypes = Lists.newArrayList(COMB_SIGN.getCode(), COMB_BUY.getCode(),
            COMB_REDEEM.getCode(), COMB_TERMINATE.getCode(),
            COMB_APPLY_BUY.getCode());

    /**
     * 是否为转入或预约转入
     *
     * @param combBusinessType
     * @return
     */
    public static boolean isBuy(String combBusinessType) {
        return CombBusTypeEnum.COMB_SIGN.getCode().equals(combBusinessType)
                || CombBusTypeEnum.COMB_BUY.getCode().equals(combBusinessType)
                || CombBusTypeEnum.COMB_APPLY_BUY.getCode().equals(combBusinessType);
    }

    /**
     * 是否为转出或预约转出
     *
     * @param combBusinessType
     * @return
     */
    public static boolean isSell(String combBusinessType) {
        return CombBusTypeEnum.COMB_TERMINATE.getCode().equals(combBusinessType)
                || CombBusTypeEnum.COMB_REDEEM.getCode().equals(combBusinessType);
    }
}
