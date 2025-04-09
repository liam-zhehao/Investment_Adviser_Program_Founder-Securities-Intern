package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

/**
 * 组合委托状态
 */
@Getter
public enum CombEntrustStatusEnum {

    INSTRUCT_UNCREATED("0", "未生成指令计划"),
    MONEY_FROZEN("A", "资金已冻结"),
    ACCOUNT_UNCREATED("B", "账户未开立"),
    MONEY_TRANSFER("C", "资金已划拨"),
    APPLICATION_CANCEL("D", "申请作废"),
    APPLICATION_SUCCESS("E", "申请成功"),
    APPLICATION_FAIL("F", "申请失败"),
    DECLARED_TO_WITHDRAW("G", "已报待撤"),
    NO_PROCESS("H", "无需处理"),
    PRE_PROCESS("I", "预受理"),
    INSTRUCT_CREATED("1", "已生成指令计划"),
    ORDER_CREATED("2", "已生成订单"),
    ORDER_SUCCESS("3", "下单成功"),
    ORDER_PARTIAL_SUCCESS("4", "部分下单成功"),
    ORDER_FAIL("5", "下单失败"),
    ORDER_WITHDRAW("6", "已撤单"),
    AFFIRM_SUCCESS("7", "确认成功"),
    AFFIRM_PARTIAL_SUCCESS("8", "部分确认成功"),
    AFFIRM_FAIL("9", "确认失败");

    private String code;

    private String desc;

    CombEntrustStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 失败状态，包括：申请作废，申请失败，下单失败，确认失败
     */
    public static List<String> failedStatuses = Lists.newArrayList(APPLICATION_CANCEL.getCode(), APPLICATION_FAIL.getCode(), ORDER_FAIL.getCode(), AFFIRM_FAIL.getCode());

    /**
     * 进行中状态，包括：未生成指令计划，已生成指令计划，下单成功，已报待撤，无需处理，预受理，已生成订单，申请成功
     */
    public static List<String> runningStatuses = Lists.newArrayList(INSTRUCT_UNCREATED.getCode(), INSTRUCT_CREATED.getCode(),
            ORDER_SUCCESS.getCode(), DECLARED_TO_WITHDRAW.getCode(), NO_PROCESS.getCode(), PRE_PROCESS.getCode(), ORDER_CREATED.getCode(), APPLICATION_SUCCESS.getCode());

    /**
     * 部分成功，包括：部分下单成功，部分确认成功
     */
    public static List<String> partialSuccessStatuses = Lists.newArrayList(ORDER_PARTIAL_SUCCESS.getCode(), AFFIRM_PARTIAL_SUCCESS.getCode());
}
