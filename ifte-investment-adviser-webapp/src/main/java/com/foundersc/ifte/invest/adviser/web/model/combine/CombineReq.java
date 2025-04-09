package com.foundersc.ifte.invest.adviser.web.model.combine;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "CombineReq", description = "目标盈kyc确认请求")
public class CombineReq {

    @NotBlank
    private String combineCode;
}
