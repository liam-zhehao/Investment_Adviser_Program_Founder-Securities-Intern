package com.foundersc.ifte.invest.adviser.dubbo.api.model.position;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombBusTypeEnum;
import com.foundersc.ifte.invest.adviser.dubbo.api.enums.CombEntrustStatusEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class CombineEntrust implements Serializable {
    private Integer initDate;

    private Integer currDate;

    private Integer currTime;

    /**
     * 组合申请编号
     */
    private String combRequestNo;


    /**
     * 原组合申请编号(撤单用)
     */
    private String origCombRequestNo;

    /**
     * 组合来源编号
     */
    private String sourceId;

    /**
     * 组合业务类型 1-组合签约，2-追加投资，3-减少投资，4-组合撤单
     */
    private String combBusinessType;

    private String clientId;

    private String investorAccount;

    private String assetUnit;

    private String combineCode;

    /**
     * 委托金额
     */
    private Double entrustBalance;

    /**
     * 赎回比例
     */
    private String redeemRatio;

    /**
     * 全部赎回标志 0-否，1-是
     */
    private String allRedeemFlag;

    /**
     * 组合申请状态
     */
    private String combRequestStatus;

    /**
     * 确认金额
     */
    private Double confirmBalance;

    /**
     * 组合名称
     */
    private String combineName;


    /**
     * 申购待确认金额
     */
    private Double appUnConfirmBalance;

    /**
     * 执行标志 1-当日待执行 2-当日已执行 3-下日待执行
     */
    private String executeFlag;

    /**
     * 撤单标志
     */
    private String cancelFlag;

    /**
     * 预计收益日期
     */
    private Integer preIncomeDate;

    /**
     * 预计到账日期
     */
    private Integer preArriveDate;

    /**
     * 赎回到账金额
     */
    private Double redeemArriveBalance;

    /**
     * 确认日期
     */
    private Integer affirmDate;


    /**
     * 预计赎回金额
     */
    private Double preRedeemBalance;

    /**
     * 预计确认日期
     */
    private Integer preAffirmDate;

    /**
     * 到账日期
     */
    private Integer arriveDate;

    /**
     * 触发日期
     */
    private Integer triggerDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否是当前表
     */
    private Boolean isCurrent;

    /**
     * 资金划拨状态
     */
    private String fundTransStatus;

    /**
     * 目标盈组合全称
     */
    private String targetCombFullName;

    /**
     * 是否为目标盈组合
     */
    private Boolean targetComb;

    /**
     * 是否已撤单
     *
     * @return
     */
    public boolean hasWithdraw() {
        return CombEntrustStatusEnum.ORDER_WITHDRAW.getCode().equals(combRequestStatus);
    }

    /**
     * 是否为预约委托
     *
     * @return
     */
    public boolean reserveEntrust() {
        return CombEntrustStatusEnum.PRE_PROCESS.getCode().equalsIgnoreCase(combRequestStatus);
    }

    /**
     * 是否为转入
     *
     * @return
     */
    public boolean isBuy() {
        return CombBusTypeEnum.isBuy(combBusinessType) && !reserveEntrust();
    }

    /**
     * 是否预约转入
     *
     * @return
     */
    public boolean isReserveBuy() {
        return CombBusTypeEnum.isBuy(combBusinessType) && reserveEntrust();
    }

    /**
     * 是否为转出
     *
     * @return
     */
    public boolean isRedeem() {
        return CombBusTypeEnum.isSell(combBusinessType) && !reserveEntrust();
    }

    /**
     * 是否为预约转出
     *
     * @return
     */
    public boolean isReserveRedeem() {
        return CombBusTypeEnum.isSell(combBusinessType) && reserveEntrust();
    }
}
