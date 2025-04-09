package com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc;

import lombok.Data;

import java.io.Serializable;

@Data
public class AnswerInfo implements Serializable {

    /**
     * 答案序号
     */
    private Long answerNo;
    /**
     * 答案内容
     */
    private String answerContent;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否启用
     */
    private String enabledFlag;

}
