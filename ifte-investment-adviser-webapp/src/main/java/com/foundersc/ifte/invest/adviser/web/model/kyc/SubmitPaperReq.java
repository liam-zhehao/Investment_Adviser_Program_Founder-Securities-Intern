package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 提交kyc问卷答案
 */
@Data
@ApiModel(value = "SubmitKycPaperReq", description = "提交KYC问卷答案")
public class SubmitPaperReq {

    @ApiModelProperty(value = "问卷版本号", required = true)
    @NotBlank
    private String paperVersion;

    @ApiModelProperty(value = "提交的问卷答案", required = true)
    @NotEmpty
    @Valid
    private List<PaperAnswer> paperAnswers;

    @ApiModelProperty(value = "投顾机构编号", required = true)
    private String investOrganNo;
}
