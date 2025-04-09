package com.foundersc.ifte.invest.adviser.web.model.trade;

import lombok.Data;

@Data
public class PurchaseResp {
    private String acpt_id;
    private String agreement_id;
    private Integer pre_affirm_date;
    private Integer init_date;
    private Integer curr_date;
    private Integer curr_time;
    private Integer pre_income_date;
    private String asset_unit;
    private String comb_request_no;
    private Integer error_no;
    private String error_info;
}
