package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wangfuwei
 * @date 2022/10/20
 */
@Data
@ApiModel(value = "EnableBalanceVO", description = "可用余额")
public class EnableBalanceVO {
    @ApiModelProperty("客户号")
    private String clientId;

    @ApiModelProperty("投顾账号")
    private String investorAccount;

    @ApiModelProperty("币种类别")
    private String moneyType;

    @ApiModelProperty("账户余额")
    private BigDecimal enableBalance;
}
