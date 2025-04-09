package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import lombok.Data;

import java.io.Serializable;

/**
 * 组合每日收益信息
 */
@Data
public class CombDailyIncome implements Serializable {
    // 日期
    private Integer date;
    //收益
    private String incomeAmount;
    private String incomeRatio;
    // 说明，例如休市等
    private String desc;
}
