package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import lombok.Data;

import java.io.Serializable;

@Data
public class CombineEntrustReq implements Serializable {
    /**
     * 组合代码
     */
    private String combineCode;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 组合申请编号
     */
    private String combRequestNo;

    /**
     * 开始时间
     */
    private Integer beginDate;

    /**
     * 结束时间
     */
    private Integer endDate;

    /**
     * 是否填充组合详细信息
     */
    private boolean fillCombFullInfo;
}
