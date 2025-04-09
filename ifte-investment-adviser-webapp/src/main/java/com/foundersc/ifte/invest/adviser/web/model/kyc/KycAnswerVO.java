package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * kyc问卷答案
 */
@Data
@ApiModel(value = "KycAnswerVO", description = "kyc问卷答案")
public class KycAnswerVO {
    @ApiModelProperty(value = "答案序号",required = true)
    private String answerNo;

    @ApiModelProperty(value = "答案内容",required = true)
    private String answerContent;

//    @ApiModelProperty(value = "是否选中",required = true)
//    private Boolean hasSelected;
}
