package com.foundersc.ifte.invest.adviser.web.model.homepage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 首页模型
 */
@Data
@ApiModel(value = "HomePageVO", description = "首页模型")
@ConfigurationProperties(prefix = "home-page")
public class HomePageVO {

    @ApiModelProperty(value = "引导语",required = true)
    private String instruction;

    @ApiModelProperty(value = "文案",required = true)
    private List<CopywritingVO> copywriting;

//    @ApiModelProperty("投顾机构列表")
//    private List<InvestOrgVO> investOrg;
}
