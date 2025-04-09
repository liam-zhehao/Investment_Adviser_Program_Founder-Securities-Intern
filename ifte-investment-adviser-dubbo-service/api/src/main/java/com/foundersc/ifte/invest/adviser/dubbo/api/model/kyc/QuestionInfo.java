package com.foundersc.ifte.invest.adviser.dubbo.api.model.kyc;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionInfo implements Serializable {

    /**
     * 试题编号
     */
    private Long questionNo;
    /**
     * 试题类型
     */
    private String questionKind;
    /**
     * 试题内容
     */
    private String questionContent;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否启用
     */
    private String enabledFlag;
    /**
     * 交易指标
     */
    private String tradeIndexType;

    /**
     * 答案列表
     */
    private List<AnswerInfo> answerList;
}
