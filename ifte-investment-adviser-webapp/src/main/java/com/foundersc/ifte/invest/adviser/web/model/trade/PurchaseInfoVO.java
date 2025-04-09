package com.foundersc.ifte.invest.adviser.web.model.trade;

import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购买页展示信息
 */
@Data
@ApiModel(value = "PurchaseInfo", description = "购买页相关信息")
public class PurchaseInfoVO extends CombineInfoVO {
    @ApiModelProperty("快捷转入金额")
    private List<FastAmountTag> fastAmountTags;

    @ApiModelProperty("账户余额")
    private BigDecimal enableBalance;

    @ApiModelProperty("转入文案提示")
    private List<String> purchaseCopywritings;

    @ApiModelProperty("电子协议url")
    private String epaperUrl;

    @ApiModelProperty("续期方式")
    private ClientTargetCombModeVO targetCombMode;

    @ApiModelProperty("交易时间标志")
    private Integer inTradeTime;
}