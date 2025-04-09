package com.foundersc.ifte.invest.adviser.web.model.combine;

import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombRiskLevelEnum;
import com.foundersc.ifte.invest.adviser.web.annotation.MetaData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CombineStrategyVO", description = "组合策略列表信息")
public class CombineStrategyVO {

    @ApiModelProperty("组合代码")
    private String combineCode;

    @ApiModelProperty("组合名称")
    private String combineName;

    @ApiModelProperty("风险等级")
    @MetaData(clazz = CombRiskLevelEnum.class)
    private String combRiskLevel;

    @ApiModelProperty("止盈收益率")
    private String targetProfitRatio;

    @ApiModelProperty("运作周期")
    private String operationPeriod;

    @ApiModelProperty("处理状态")
    private String dealStatus;

    @ApiModelProperty("处理状态描述")
    private String dealStatusDesc;

    @ApiModelProperty("购买截止日期提醒")
    private String buyEndTip;

    @ApiModelProperty("购买开始日期")
    private String buyStartDate;
}
