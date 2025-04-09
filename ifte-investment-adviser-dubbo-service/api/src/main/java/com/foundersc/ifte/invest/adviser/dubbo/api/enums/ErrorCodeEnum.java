package com.foundersc.ifte.invest.adviser.dubbo.api.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 错误编码
 * code大于1000的为具体的业务异常，小于1000的为系统异常或者通用异常
 */
@Getter
public enum ErrorCodeEnum {
    UNKNOWN("0", "未知错误"),
    PARAM_NULL("1", "参数不能为空"),
    COMB_CODE_ERROR("1001", "组合代码错误"),
    QUERY_CODE_INFO_ERROR("1002", "查询组合信息失败"),
    RANGE_ID_ERROR("1003", "rangeId传参错误"),
    SETUP_DATE_ERROR("1004", "组合成立日期错误"),
    QUERY_COMB_HIS_PRICE_ERROR("1005", "查询组合行情历史信息失败"),
    INVEST_ORGAN_NO_ERROR("1006", "投顾机构编号错误"),
    QUERY_KYC_PAPER_ERROR("1007", "查询kyc问卷错误"),
    QUERY_TESTJOUR_ERROR("1008", "查询答题记录错误"),
    POST_PAPER_ANSWER_ERROR("1009", "提交问卷答案错误"),
    QUERY_COMBINFOS_BY_KYC_ERROR("1010", "查询组合推荐列表错误"),
    QUERY_COMB_POS_ERROR("1011", "查询组合持仓详情失败"),
    QUERY_TRADE_DATE_RULE_ERROR("1012", "查询交易时间规则失败"),
    QUERY_COPY_WRITING_ERROR("1013", "查询文案信息失败"),
    QUERY_INVESTOR_ACCOUNT_ERROR("1014", "查询投顾账户信息失败"),
    QUERY_TOTAL_ASSET_ERROR("1015", "查询总资产失败"),
    QUERY_COMB_ASSET_ERROR("1016", "查询组合资产失败"),
    QUERY_CURR_ENTRUST_ERROR("1017", "查询当前委托失败"),
    QUERY_COMB_SHARE_ERROR("1018", "查询产品成分信息失败"),
    QUERY_COMB_HIS_ASSET_ERROR("1019", "查询组合历史资产失败"),
    QUERY_COMB_FETCH_ASSET_ERROR("1020", "查询客户组合可取资产失败"),
    QUERY_COMB_FARE_ARG_ERROR("1021", "查询组合服务费参数失败"),
    QUERY_COMB_ELIG_ERROR("1022", "查询组合适当性信息失败"),
    COMB_REQUEST_NO_NULL("1023", "组合申请查询结果为空"),
    COMB_STRATEGY_LIST_ERROR("2001", "组合策略信息查询失败"),
    COMB_PROFIT_TYPE_MISS_ERROR("2002", "组合收益类型缺失"),

    ;

    ErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    @Override
    public String toString() {
        return StringUtils.join("Error_", code, "【", desc, "】");
    }

}
