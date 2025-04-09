package com.foundersc.ifte.invest.adviser.web.model.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.web.enums.TradeFlowEnum.*;
import static com.foundersc.ifte.invest.adviser.web.util.TargetDateUtil.*;

/**
 * 赎回结果
 */
@Data
@ApiModel(value = "RedeemResultVO", description = "赎回结果")
public class RedeemResultVO {
    @ApiModelProperty("组合申请编号")
    private String combRequestNo;

    @ApiModelProperty("申请编号，解约时返回")
    private String requestNo;

    @ApiModelProperty("赎回标题")
    private String title;

    @ApiModelProperty("赎回描述信息")
    private String desc;

    @ApiModelProperty(value = "转出时间轴")
    private List<FlowListVO> flowList;

    public RedeemResultVO(String combRequestNo) {
        this.combRequestNo = combRequestNo;
    }

    public RedeemResultVO(String combRequestNo, String requestNo) {
        this.combRequestNo = combRequestNo;
        this.requestNo = requestNo;
    }

    public RedeemResultVO(String combRequestNo, String requestNo, String title, String desc) {
        this.combRequestNo = combRequestNo;
        this.requestNo = requestNo;
        this.title = title;
        this.desc = desc;
    }

    public static List<FlowListVO> getTradeTimeFlow(String currTime, String affirmDate, String arriveDate) {
        List<FlowListVO> flowListVOs = new ArrayList<>();
        FlowListVO acceptFlow = new FlowListVO(PURCHASE_ACCEPT.getFlowTitle(), currTime, true);
        FlowListVO affirmFlow = new FlowListVO(REDEEM_AFFIRM.getFlowTitle(), PREDICT.concat(getFormatEntrustDate(affirmDate)), false);
        FlowListVO incomeFlow = new FlowListVO(REDEEM_ARRIVE.getFlowTitle(), PREDICT.concat(getFormatEntrustDate(arriveDate)).concat(REDEEM_ARRIVE_DESC), false);
        flowListVOs.add(acceptFlow);
        flowListVOs.add(affirmFlow);
        flowListVOs.add(incomeFlow);
        return flowListVOs;
    }
}
