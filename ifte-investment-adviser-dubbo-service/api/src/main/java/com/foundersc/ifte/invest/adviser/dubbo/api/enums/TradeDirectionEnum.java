package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import lombok.Getter;

/**
 * 交易方向
 *
 * @author wangfuwei
 * @date 2022/9/26
 */
@Getter
public enum TradeDirectionEnum {
    /**
     * 无交易
     */
    NONE(0),

    /**
     * 买
     */
    PURCHASE(1),

    /**
     * 卖
     */
    REDEEM(2),

    /**
     * 买、卖
     */
    PURCHASE_REDEEM(3);

    private Integer code;

    TradeDirectionEnum(int code) {
        this.code = code;
    }

    public static TradeDirectionEnum parseByCode(Integer code) {
        for (TradeDirectionEnum value : TradeDirectionEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return NONE;
    }

    /**
     * 是否为买或卖
     *
     * @param code
     * @return
     */
    public static boolean isTradeOpe(Integer code) {
        TradeDirectionEnum tradeDirectionEnum = parseByCode(code);
        return PURCHASE.equals(tradeDirectionEnum) || REDEEM.equals(tradeDirectionEnum);
    }
}
