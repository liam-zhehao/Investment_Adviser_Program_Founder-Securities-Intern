package com.foundersc.ifte.invest.adviser.web.constants;

/**
 * 文案相关
 *
 * @author wangfuwei
 * @date 2022/9/30
 */
public class TipConstants {
    private TipConstants() {
    }

    /**
     * 平安证券投顾机构编码
     */
    private final static String PING_AN_INVEST_ORG_NO = "1101";

    public static final String PURCHASE_AFFIRM_TIP = "确认日期：现在转入预计%s（%s）确认份额（QDII基金除外）。";

    public static final String REDEEM_AFFIRM_TIP = "确认日期：15点前转出，成分基金将按%s（%s）净值计算金额，预计%s（%s）到账（QDII基金除外）";

    public static final String PURCHASE_RATIO_TIP = "温馨提示：投顾服务费%s/年由投顾机构收取，基金交易相关费用按照基金销售业务规则，由成分基金管理人或基金销售机构收取。";

    public static final String REDEEM_RATIO_TIP = "温馨提示：基金交易相关费用按照基金销售业务规则，由成分基金管理人或基金销售机构收取。若持有时长少于7天（不含），基金公司将收取1.5%的赎回手续费，方正证券不额外收费";

    public static final String LESS_THAN_MIN_AMOUNT = "低于最小转入金额，购买失败";

    public static final String LESS_THAN_MIN_RATIO = "低于最小转出比例，转出失败";

    public static final String MORE_THAN_MAX_AMOUNT = "高于最大转入金额，购买失败";

    public static final String MORE_THAN_MAX_RAIO = "高于最大转出比例，转出失败";

    public static final String NOT_SIGNED_EPAPER = "未签署电子协议，购买失败";

    public static final String REDEEM_RATIO_TIP_1 = "可转出%s~%s";

    public static final String REDEEM_RATIO_TIP_2 = "可转出%s~%s或全部";

    public static final String REDEEM_RATIO_TIP_3 = "可转出%s或全部";

    public static final String REDEEM_RATIO_TIP_4 = "可转出全部";

    public static final String REDEEM_RATIO_TIP_5 = "可转出%s~全部";

    public static final String REDEEM_RATIO_TIP_6 = "可转出%s";

    public static final String NOT_ALLOW_REDEEM = "无可转出份额";

    public static final String NOT_ALLOW_REDEEM_ALL = "不允许全部转出";

    public static final String PURCHASE_TIP = "%s元起，投顾服务费%s/年";

    public static final String RISK_LEVEL_TIP = "您的风险承受等级是：%s\n该策略产品的风险等级是：%s";

    public static final String CLIENT_RISK_LEVEL_TIP = "您的风险承受等级是：%s";

    public static final String COMB_RISK_LEVEL_TIP = "该策略产品的风险等级是：%s";

    public static final String RISK_LEVEL_NOT_MATCH_CANNOT_BUY_TIP = "已超出您的风险承受能力，暂不能购买";

    public static final String RE_EVALUATE_TIP = "若您的风险信息已变更，请点击下方按钮重新测评，每日仅可测评 1 次";

    public static final String CLIENT_INVEST_KIND_TERM_TIP = "您拟投资的品种是：%s\n您拟投资的期限是：%s";

    public static final String CLIENT_INVEST_KIND_TIP = "您拟投资的品种是：%s";

    public static final String CLIENT_INVEST_TERM_TIP = "您拟投资的期限是：%s";

    public static final String COMB_INVEST_KIND_TERM_TIP = "该策略产品投资的品种是：%s\n该策略产品投资的期限是：%s";

    public static final String COMB_INVEST_KIND_TIP = "该策略产品投资的品种是：%s";

    public static final String COMB_INVEST_TERM_TIP = "该策略产品投资的期限是：%s";

    public static final String INVEST_KIND_TERM_MATCH_RESULT_TIP = "策略品种与您拟投资的品种：%s\n策略期限与您拟投资的期限：%s";

    public static final String INVEST_KIND_MATCH_RESULT_TIP = "策略品种与您拟投资的品种：%s";

    public static final String INVEST_TERM_MATCH_RESULT_TIP = "策略期限与您拟投资的期限：%s";


    public static final String CONTINUE_BUY_TIP = "若选择继续转入即表明您已知悉上述提示，并已了解其风险自愿进行购买";

    public static final String RISK_EVAL_TIP = "若您的风险信息已变更，请点击下方按钮重新测评，每日仅可测评 1 次";

    public static final String INVEST_KIND_TIP_NOT_MATCH_CANNOT_BUY_TIP = "投资品种或投资期限不匹配，暂不能购买";

    public static final String LEGAL_STATEMENTS = "风险提示: 基金有风险，投资需谨慎。基金投顾机构不保证基金投资组合策略一定盈利及最低收益，也不做保本承诺，投资者存在无法获得收益甚至本金亏损的风险。因转入转出时间不同、相关基金交易限制、基金交易费用、组合调仓等原因，投资者持有的基金投资组合与策略所对应的标准组合之间可能存在差异，并由此可能导致业绩表现等存在差异。" +
            "策略内可能包含基金投顾机构或其关联方管理、托管或销售的产品。策略可投资的基金产品受到合作机构基金销售范围等多种因素的影响，可能最终影响策略的业绩表现。策略与单只基金产品的风险特征存在差异，投资者参与投资前应认真阅读基金投顾服务协议、风险揭示书及策略说明书等文件，在全面了解策略风险收益特征、运作特点及适当性匹配意见的基础上谨慎作出投资决策。" +
            "基金投顾业务尚处于试点阶段，基金投顾机构存在因试点资格被取消不能继续提供服务的风险。";

    public static final String TARGET_LEGAL_STATEMENT1 = "风险提示: 基金有风险，投资需谨慎。目标盈策略并不必然代表投资者买入或持有的策略一定会在运作期内达到目标止盈年化收益率，会存在运作期内未达标、甚至出现本金亏损的可能。目标止盈年化收益率为策略维度的年化收益率，并非策略的累计收益率，不预示未来业绩表现，不代表投顾组合的未来收益。目标盈策略自动续期功能指投资者持有的目标盈策略如若达到止盈目标或运作期满对于赎回资金的处理方式，以投顾机构发布的届时生效的业务规则为准。";

    public static final String TARGET_LEGAL_STATEMENT2 = "基金投顾机构不保证基金投资组合策略一定盈利及最低收益，也不做保本承诺，投资者存在无法获得收益甚至本金亏损的风险。策略内可能包含基金投顾机构或其关联方管理、托管或销售的产品。策略与单只基金产品的风险特征存在差异，投资者参与投资前应认真阅读基金投顾服务协议、风险揭示书及策略说明书等文件，在全面了解策略风险收益特征、运作特点及适当性匹配意见的基础上谨慎作出投资决策。基金投顾业务尚处于试点阶段，基金投顾机构存在因试点资格被取消不能继续提供服务的风险。";

    public static final String STATEMENT_FROM = "本页面非任何法律文件，各项数据仅供参考，不预示未来收益。基金投顾服务由%s提供，成分基金销售服务由方正证券提供。";

    public static final String NOT_SHOW_TIP = "成立不满1年暂不展示";

    public static final String DAILY_INCOME_RATIO_DESC = "日涨幅";

    public static final String BLANK_STRING = "--";

    public static final String YEAR_INCOME_RATIO = "年化收益率";

    public static final String TARGET_KYC_TITLE = "请您确定以下信息是否符合您的投资意向";

    public static final String PROFIT_MODE_TIP1 = "除止盈或到期当天，其余时间均可修改并实时生效。";

    public static final String PROFIT_MODE_TIP2 = "安心小目标系列每一期组合可采用不同的策略。如投资者选择“自动续期”，则视为认可后续各期安心小目标系列的组合策略说明书及其他补充文件，并自动参与最近一期处于参与期的安心小目标组合，具体止盈收益率以当期实际为准。";

    public static final String NON_TRADE_TIME_TIP = "非交易时间转入提示：1.我司接受您在非交易时间的委托申请，但不代表您转入成功。2.您的资金在非交易时间不会被冻结或者占用。";

    public static final String PURCHASE_RESULT_TITLE = "转入已受理";

    public static final String REDEEM_RESULT_TITLE = "转出已受理";

    public static final String PRE_PURCHASE_RESULT_TITLE = "预约转入已提交";

    public static final String PURCHASE_RESULT_TIP = "该笔交易成交确认请以管理人为准，您可以在“我的理财-交易记录”中查询最新状态";

    public static final String TARGET_BUY_END_TIP = "%s月%s日截止参与";

    public static final String NON_TRADE_TIME_TOAST = "当前为非交易时间，暂不支持转入";

    public static final String TRANS_TIP = "当前为非交易时间，可开通差额银证转账功能，提升转入成功率";

    public static final String NO_AUTO_TRANS_TIP = "当前为非交易时间，预计下一交易日发起扣款并转入，若届时可用资金不足，将短信通知您";

    /**
     * 获取续期方式提示
     *
     * @param investOrgNo
     * @return
     */
    public static String getProfitModeTip(String investOrgNo) {
        if (PING_AN_INVEST_ORG_NO.equalsIgnoreCase(investOrgNo)) {
            return PROFIT_MODE_TIP2 + "\n\n" + PROFIT_MODE_TIP1;
        }
        return PROFIT_MODE_TIP1;
    }
}
