package com.foundersc.ifte.invest.adviser.web.model.kyc;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ApiModel(value = "InvestOrganInfos", description = "已上架的投顾机构列表")
@ConfigurationProperties(prefix = "invest-organ-infos")
public class InvestOrganInfos {

    private List<InvestOrganVO> investOrgans;

}
