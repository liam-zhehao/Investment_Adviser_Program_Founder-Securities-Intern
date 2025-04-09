package com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc;

import lombok.Data;
import java.io.Serializable;

/**
 * 答题记录
 */
@Data
public class TestjourInfo implements Serializable {

    /**
     * 初始化日期
     */
    private Integer initDate;
    /**
     * 流水序号
     */
    private Long serialNo;
    /**
     * 客户ID
     */
    private String clientId;
    /**
     * 投顾账户
     */
    private String investorAccount;
    /**
     * 问卷类别
     */
    private Integer paperType;
    /**
     * 试卷答案
     */
    private String paperAnswer;
    /**
     * 清算日期
     */
    private Integer dateClear;
    /**
     * 当前时间
     */
    private Integer currTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 定位串
     */
    private String positionStr;
    /**
     * 问卷版本号
     */
    private String paperVersion;
    /**
     * 组合代码
     */
    private String combineCode;
}
