package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import lombok.Getter;

/**
 * @author wangfuwei
 * @date 2022/10/18
 */
@Getter
public enum CopywritingEnum {
    PURCHASE_RULE("1", "转入规则"),
    REDEEM_RULE("2", "转出规则"),
    CHANGE_POS_RULE("3", "调仓规则"),
    FEE_DESC("4", "费用说明"),
    ADVISE_FEE("5", "投顾服务费"),
    PURCHASE_REDEEM_FEE("6", "申购、赎回费用"),
    FUND_TRADE_FEE("7", "基金交易费用"),
    TRADE_FEE("8", "交易费用"),
    RULE_DETAIL("9", "规则详情"),
    FIXED_TERM_CHANGE_POS("10", "定期调仓"),
    UNFIXED_TERM_CHANGE_POS("11", "不定期调仓"),
    CHANGE_POS("12", "");
    private String code;
    private String desc;

    CopywritingEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
