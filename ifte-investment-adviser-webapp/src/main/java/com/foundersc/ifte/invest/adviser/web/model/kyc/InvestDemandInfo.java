package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 投资需求
 */
@Data
@ApiModel(value = "InvestDemandInfo", description = "投资需求")
public class InvestDemandInfo {

    @ApiModelProperty(value = "投资需求列表")
    private List<InvestDemandVO> investDemandVOS;

    @ApiModelProperty(value = "只有一道题时候的投资描述")
    private String oneDemandDesc;

}
