package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "TimeCheckResultVO", description = "交易时间校验结果")
@Data
public class TimeCheckResultVO {

    /**
     * 1-支持 0-不支持
     */
    @ApiModelProperty("是否支持7*24小时")
    private Integer canAllTimeTrade;

    @ApiModelProperty("是否为交易时间")
    private Integer isTradeTime;

    @ApiModelProperty("转账提醒")
    private String transTip;

    @ApiModelProperty("银行卡是否支持自动转账")
    private Integer canAutoTrans;

    @ApiModelProperty("自动转账协议地址")
    private String autoTransContractUrl;

    @ApiModelProperty("银行信息")
    private String bankInfo;

    @ApiModelProperty("预计扣款日期")
    private String confirmDate;
}
