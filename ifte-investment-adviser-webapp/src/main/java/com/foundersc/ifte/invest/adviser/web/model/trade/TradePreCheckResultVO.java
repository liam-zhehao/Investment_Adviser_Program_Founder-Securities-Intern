package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易前检查
 *
 * @author wangfuwei
 * @date 2022/9/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradePreCheckResultVO {
    @ApiModelProperty("是否允许交易")
    private Boolean canTrade;


    @ApiModelProperty("不允许交易的提示文案")
    private String cannotTradeTip;

    @ApiModelProperty("是否在交易时间")
    private Boolean inTradeTime;

    public static TradePreCheckResultVO cannotTrade(String tip) {
        return new TradePreCheckResultVO(false, tip, null);
    }

    public static TradePreCheckResultVO canTrade() {
        return new TradePreCheckResultVO(true, null,null);
    }
}
