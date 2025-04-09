package com.foundersc.ifte.invest.adviser.web.model.trade;

import com.foundersc.ifte.invest.adviser.web.util.DataUtil;
import com.foundersc.ifte.invest.adviser.web.util.RatioUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.ONE_HUNDRED;

@Data
@ApiModel(value = "FastAmountTag", description = "购买赎回金额标签")
@ToString
public class FastAmountTag {

    @ApiModelProperty("金额数值")
    private BigDecimal value;

    @ApiModelProperty("转入金额标签")
    private String purchaseDisplay;

    @ApiModelProperty("赎回比例标签")
    private String redeemDisplay;

    public String getPurchaseDisplay() {
        return String.valueOf(value.longValue());
    }

    public String getRedeemDisplay() {
        if (ONE_HUNDRED.equals(value)) {
            return "全部";
        }
        return RatioUtil.formatPercent(value.divide(ONE_HUNDRED), DataUtil.getDotNum(value, 2));
    }

    public FastAmountTag(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value.setScale(DataUtil.getDotNum(value));
    }
}
