package com.foundersc.ifte.invest.adviser.web.model.trade;

import com.foundersc.ifte.invest.adviser.web.model.combine.CombineInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 赎回页面相关信息
 */
@Data
@ApiModel(value = "redeemPageInfo", description = "赎回页相关信息")
public class RedeemInfoVO extends CombineInfoVO {
    @ApiModelProperty("转出比例范围提示: 可转出5%-60%或全部")
    private String redeemTip;

    @ApiModelProperty("转出比例下限，如5，代表5.5%")
    private BigDecimal minFetchRatio;

    @ApiModelProperty("转出比例上限，如50，代表50%")
    private BigDecimal maxFetchRatio;

    @ApiModelProperty("是否可以全部赎回")
    private Boolean allowAllRedeem;

    @ApiModelProperty("全部可转出比例")
    private BigDecimal allRedeemRatio;

    @ApiModelProperty("是否允许赎回")
    private Boolean enRedeem;

    @ApiModelProperty("组合资产")
    private BigDecimal combAsset;

    @ApiModelProperty("预估赎回总资产")
    private BigDecimal predictRedeemAsset;

    @ApiModelProperty("最小可取金额")
    private BigDecimal minFetchBalance;

    @ApiModelProperty("最大可取金额")
    private BigDecimal maxFetchBalance;

    @ApiModelProperty("快捷赎回比例")
    private List<FastAmountTag> fastRatioTags;

    @ApiModelProperty("转出文案提示")
    private List<String> redeemCopyWrites;

}