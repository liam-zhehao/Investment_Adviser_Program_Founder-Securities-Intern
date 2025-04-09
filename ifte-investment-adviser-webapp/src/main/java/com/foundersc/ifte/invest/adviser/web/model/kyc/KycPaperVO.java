package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * kyc问卷
 */
@Data
@ApiModel(value = "KycPaperVO", description = "kyc问卷")
public class KycPaperVO {
    @ApiModelProperty(value = "问卷版本号", required = true)
    private String paperVersion;

    @ApiModelProperty(value = "试题配置", required = true)
    private List<KycQuestionVO> questionList;
}
