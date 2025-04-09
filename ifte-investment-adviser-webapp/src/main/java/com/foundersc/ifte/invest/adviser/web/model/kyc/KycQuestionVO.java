package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * kyc问题及选项
 */
@Data
@ApiModel(value = "KycQuestionVO", description = "kyc问题及选项")
public class KycQuestionVO {
    @ApiModelProperty(value = "试题编号", required = true)
    private String questionNo;

    @ApiModelProperty(value = "试题类型: 0单选 1多选 3可编辑", required = true)
    private String questionKind;

    @ApiModelProperty(value = "试题内容", required = true)
    private String questionContent;

    private String remark;

    @ApiModelProperty(value = "答案配置", required = true)
    private List<KycAnswerVO> answerList;

    @ApiModelProperty(value = "用户上次选中的答案", required = true)
    private List<String> lastAnswers;
}
