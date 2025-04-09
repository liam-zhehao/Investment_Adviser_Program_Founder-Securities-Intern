package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 提交的答案
 */
@Data
@ApiModel(value = "PaperAnswer", description = "提交的答案")
public class PaperAnswer {

    @ApiModelProperty(value = "试题编号", required = true)
    @NotBlank
    private String questionNo;

    @ApiModelProperty(value = "选择的答案编号，单选时只有一个元素，多选时多个元素。", required = true)
    @NotEmpty
    private List<String> answerNos;
}
