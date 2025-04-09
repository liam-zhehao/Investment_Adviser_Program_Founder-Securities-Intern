package com.foundersc.ifte.invest.adviser.web.model.trade;

import com.foundersc.itc.product.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.web.enums.TradeFlowEnum.*;
import static com.foundersc.ifte.invest.adviser.web.util.TargetDateUtil.PREDICT;
import static com.foundersc.ifte.invest.adviser.web.util.TargetDateUtil.getFormatEntrustDate;

/**
 * 转入结果
 */
@Data
@ApiModel(value = "PurchaseResultVO", description = "转入提交结果")
public class PurchaseResultVO {
    @ApiModelProperty(value = "受理单编号")
    private String acptId;

    @ApiModelProperty(value = "组合申请编号")
    private String combRequestNo;

    @ApiModelProperty(value = "交易时间标志")
    private Integer isTradeTime;

    @ApiModelProperty("转入标题")
    private String title;

    @ApiModelProperty("转入描述信息")
    private String desc;

    @ApiModelProperty(value = "转入时间轴")
    private List<FlowListVO> flowList;

    public PurchaseResultVO(String acptId, String combRequestNo, String title, String desc) {
        this.acptId = acptId;
        this.combRequestNo = combRequestNo;
        this.title = title;
        this.desc = desc;

    }

    public PurchaseResultVO(String combRequestNo) {
        this.combRequestNo = combRequestNo;
    }

    public static List<FlowListVO> getTradeTimeFlow(String currTime, String affirmDate, String incomeDate) {
        List<FlowListVO> flowListVOs = new ArrayList<>();
        FlowListVO acceptFlow = new FlowListVO(PURCHASE_ACCEPT.getFlowTitle(), currTime, true);
        FlowListVO affirmFlow = new FlowListVO(PURCHASE_AFFIRM.getFlowTitle(), PREDICT.concat(getFormatEntrustDate(affirmDate)), false);
        FlowListVO incomeFlow = new FlowListVO(PURCHASE_INCOME.getFlowTitle(), PREDICT.concat(getFormatEntrustDate(incomeDate)), false);
        flowListVOs.add(acceptFlow);
        flowListVOs.add(affirmFlow);
        flowListVOs.add(incomeFlow);
        return flowListVOs;
    }

    public static List<FlowListVO> getNonTradeTimeFlow(String currTime, String acceptDate, String affirmDate, String incomeDate) {
        List<FlowListVO> flowListVOs = new ArrayList<>();
        String currentDate = DateUtils.getCurrentIntegerDate().toString();
        String currFlowTitle = currentDate.equals(acceptDate) ? PRE_CURR_ACCEPT.getFlowTitle() : PRE_NEXT_ACCEPT.getFlowTitle();
        FlowListVO currFlow = new FlowListVO(currFlowTitle, currTime, true);
        FlowListVO acceptFlow = new FlowListVO(PURCHASE_ACCEPT.getFlowTitle(), PREDICT.concat(getFormatEntrustDate(acceptDate)), false);
        FlowListVO affirmFlow = new FlowListVO(PURCHASE_AFFIRM.getFlowTitle(), PREDICT.concat(getFormatEntrustDate(affirmDate)), false);
        FlowListVO incomeFlow = new FlowListVO(PURCHASE_INCOME.getFlowTitle(), PREDICT.concat(getFormatEntrustDate(incomeDate)), false);
        flowListVOs.add(currFlow);
        flowListVOs.add(acceptFlow);
        flowListVOs.add(affirmFlow);
        flowListVOs.add(incomeFlow);
        return flowListVOs;
    }
}
