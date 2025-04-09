package com.foundersc.ifte.invest.adviser.web.model.combine;

import com.foundersc.ifc.portfolio.t2.enums.v2.elig.CombRiskLevelEnum;
import com.foundersc.ifte.invest.adviser.web.annotation.MetaData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 组合推荐
 */
@Data
@ApiModel(value = "RecommendCombineVO", description = "推荐组合")
public class RecommendCombineVO {
    @ApiModelProperty(value = "组合名称", required = true)
    private String combineName;

    @ApiModelProperty(value = "组合代码", required = true)
    private String combineCode;

    @ApiModelProperty(value = "投顾机构名称", required = true)
    private String investOrganName;

    @ApiModelProperty(value = "投顾机构编号", required = true)
    private String investOrganNo;

    @ApiModelProperty("组合来源编号")
    private String sourceId;

    @ApiModelProperty(value = "组合简介", required = false)
    private String combineDesc;

    @ApiModelProperty(value = "组合管理模式", required = true)
    private String combEntrustMode;

    @ApiModelProperty(value = "组合风险等级", required = true)
    @MetaData(clazz = CombRiskLevelEnum.class)
    private String combRiskLevel;

    @ApiModelProperty(value = "组合年化收益率", required = true)
    private String comIncomeRatio;

    @ApiModelProperty(value = "组合最大回撤率", required = true)
    private String comMaxRetreatRatio;

    @ApiModelProperty(value = "运营标签")
    private List<String> opeTags;

    @ApiModelProperty(value = "是否成立满一年")
    private Boolean setUpMoreThanOneYear;

    @ApiModelProperty(value = "展示值")
    private String displayValue;

    @ApiModelProperty(value = "展示描述")
    private String displayDesc;
}
