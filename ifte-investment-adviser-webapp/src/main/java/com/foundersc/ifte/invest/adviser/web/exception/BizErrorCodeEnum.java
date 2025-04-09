package com.foundersc.ifte.invest.adviser.web.exception;

import lombok.Getter;

/**
 * 业务错误码
 */
@Getter
public enum BizErrorCodeEnum {
    UNKNOWN("500", "未知错误"),
    PARAM_ERROR("501", "参数错误"),
    RPC_ERROR("502", "Rpc异常"),
    T2_SERVICE_ERROR("2000", "调用t2服务错误"),
    COMB_SETUP_DATE_ERROR("2001", "组合成立日期缺失"),
    QUERY_KYC_PAPER_ERROR("2002", "获取kyc文件失败"),
    KYC_PAPER_ANSWER_ERROR("2003", "kyc问卷提交格式错误"),
    EXCHTIME_TRANS_CHECK_ERROR("2004", "是否允许交易检查失败"),
    QUERY_COMB_AGREEMENT_ERROR("2005", "组合签约查询失败"),
    QUERY_EXCH_TIME_ERROR("2006", "查询投顾系统交易时间失败"),
    QUERY_ENABLE_BALANCE_ERROR("2007", "查询账户余额失败"),
    QUERY_TRADE_DATE_RULE_ERROR("2008", "查询组合交易规则日期失败"),
    QUERY_COMB_FARE_ARG_ERROR("2009", "查询组合服务费参数失败"),
    QUERY_EPAPER_ERROR("2010", "查询电子协议失败"),
    DOWNLOAD_EPAPER_TEMPLATE_ERROR("2011", "获取电子协议内容失败"),
    SIGN_EPAPER_ERROR("2012", "签署电子协议失败"),
    QUERY_EPAPER_SIGN_SERIAL_ERROR("2013", "查询电子协议签署流水失败"),
    SIGN_AGREEMENT_ERROR("2014", "组合签约失败"),
    ADD_INVEST_ERROR("2015", "组合追加投资失败"),
    RECALL_COMB_REQUEST_ERROR("2016", "组合申请撤单失败"),
    QUERY_ASSET_FETCH_EXT_ERROR("2017", "查询客户组合可取资产失败"),
    REDUCE_INVEST_ERROR("2018", "组合减少投资失败"),
    CANCEL_AGREEMENT_ERROR("2019", "组合解约失败"),
    PURCHASE_ERROR("2020", "购买失败"),
    REDEEM_ERROR("2021", "赎回失败"),
    ELIG_CHECK_ERROR("2022", "客户组合适当性校验失败"),
    QUERY_CLIENT_PREFER_ERROR("2023", "客户风险偏好查询失败"),
    QUERY_T2_CLIENT_INFO_ERROR("2024", "查询客户信息失败"),
    NO_RISK_EVAL("2025", "您的风险评测缺失\n您可在小方App的“业务办理-风险评测”中进行评测"),
    RISK_EVAL_OVERDUE("2026", "风险评测过期\n您可在小方App的“业务办理-风险评测”中重新进行评测"),
    INVEST_TERM_NOT_MATCH("2027", "投资期限不匹配\n您可在小方App的“业务办理-风险评测”中重新进行评测"),
    INVEST_KIND_NOT_MATCH("2028", "投资品种不匹配\n您可在小方App的“业务办理-风险评测”中重新进行评测"),
    RISK_LEVEL_NOT_MATCH("2029", "风险等级不匹配\n您可在小方App的“业务办理-风险评测”中重新进行评测"),
    ID_OVERDUE("2030", "身份证过期\n您可以在小方App的“业务办理-个人资料更新”中完善个人信息"),
    SENILE_NOT_ALL_TRADE("2031", "超龄不允许下单"),
    QUERY_HIS_EPAPER_SIGN_SERIAL_ERROR("2032", "查询电子协议历史签署流水失败"),
    INIT_INVEST_ORGAN_ERROR("2033", "初始化投顾机构信息失败"),
    COMB_PROFIT_TYPE_ERROR("2034", "组合收益类型缺失"),
    MODIFY_TARGET_COMB_MODE_FAIL("2035", "修改目标盈续期方式失败"),
    COMB_PROFIT_TYPE_NOT_SUPPORT("2036", "当前组合收益类型不支持"),
    COMB_TAG_QUERY_ERROR("2037", "组合KYC标签查询失败"),
    TARGET_KYC_SIGN_ERROR("2038", "目标盈KYC签署失败"),
    QUERY_CLIENT_BANK_ERROR("2039", "银行卡信息查询失败"),
    PRE_ADD_INVEST_ERROR("2040", "目标盈组合追加投资失败"),
    TRADE_TYPE_ERROR("2041", "交易类型不存在"),
    NOT_ALLOW_RECALL_COMB_REQUEST("2042", "该申请不能撤单"),

    REPEAT_SUBMIT("4001", "重复提交"),
    ;

    BizErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;
}
