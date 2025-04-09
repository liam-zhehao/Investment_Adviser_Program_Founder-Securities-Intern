package com.foundersc.ifte.invest.adviser.web.enums;

import lombok.Getter;

@Getter
public enum TradeFlowEnum {
    PRE_CURR_ACCEPT("1", "预计今日扣款并受理"),
    PRE_NEXT_ACCEPT("2", "预计下一交易日扣款并受理"),
    PURCHASE_ACCEPT("3", "交易受理"),
    PURCHASE_AFFIRM("4", "确认份额"),
    PURCHASE_INCOME("5", "查看盈亏"),
    REDEEM_AFFIRM("6", "确认金额"),
    REDEEM_ARRIVE("7", "预计到账");

    private String flowType;

    private String flowTitle;

    TradeFlowEnum(String flowType, String flowTitle) {
        this.flowType = flowType;
        this.flowTitle = flowTitle;
    }
}
