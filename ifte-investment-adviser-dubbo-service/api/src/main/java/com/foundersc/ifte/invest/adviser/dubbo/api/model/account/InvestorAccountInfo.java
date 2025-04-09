package com.foundersc.ifte.invest.adviser.dubbo.api.model.account;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvestorAccountInfo implements Serializable {
    private String clientId;

    /**
     * 投顾账户状态
     * 1	已签约
     * 2	已解约
     * 3	未确认
     * 4	确认失败
     * 5	解约中
     * 6	待回访
     */
    private String investorAccountStatus;

    /**
     * 投顾账户类型
     * 0	默认
     * 1	管理型账户
     * 2	建议型账户
     */
    private String investorAccountType;

    private String accountName;

    private String investorAccount;

    /**
     * 开户日期
     */
    private int openDate;
    /**
     * 结束日期
     */
    private int endDate;
    /**
     * 开市时间
     */
    private int openTime;
    /**
     * 结束时间
     */
    private int endTime;
}
