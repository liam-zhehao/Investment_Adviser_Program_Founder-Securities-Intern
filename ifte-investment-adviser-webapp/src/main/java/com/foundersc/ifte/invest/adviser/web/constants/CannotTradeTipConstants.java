package com.foundersc.ifte.invest.adviser.web.constants;

/**
 * @author wangfuwei
 * @date 2022/9/29
 */
public class CannotTradeTipConstants {
    private CannotTradeTipConstants() {}

    public static final String COMB_CODE_STATUS_ERROR = "组合状态未生效";

    public static final String COMB_SHELF_STATUS_ERROR = "组合未上架";

    public static final String NOT_ALLOW_TRADE_STATUS = "当前组合不允许交易";

    public static final String ALLOW_TRADE_TIME = "当前处于非交易时间，\n请在交易日%s-%s再来";

    public static final String CANCELLING_NOT_ALLOW_TRADE = "解约中无法交易";

    public static final String TARGET_COMB_CAN_NOT_PURCHASE = "非参与期不允许购买";

    public static final String USE_NEW_VERSION_TIP = "请更新至最新版App操作";
}
