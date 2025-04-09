package com.foundersc.ifte.invest.adviser.dubbo.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CombAgreement implements Serializable {

    private String clientId;

    private String combineCode;
    /**
     * 协议状态 0-未生效,1-已生效,2-已失效,3-已作废,4-已过期,5-待回访,6-解约中,7-暂停
     */
    private String agreementStatus;

    private int signDate;
}
