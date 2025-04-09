package com.foundersc.ifte.invest.adviser.dubbo.constant;

public class CommonConstants {
    private CommonConstants() {
    }

    /**
     * 初始页码
     */
    public static final int INIT_PAGE_NO = 1;

    /**
     * 默认页码
     */
    public static final int PAGE_SIZE = 500;

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 保留天整数日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";

    /**
     * 理财信息日期格式
     */
    public static final String FIN_INFO_DATE_FORMAT = "yyyy.MM.dd";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String TODAY = "今日";

    public static final String VIEW_PROFIT_DATE = "%s 净值更新后";

    /**
     * 月-日
     */
    public static final String MD_DATE_TIME_FORMAT = "MM-dd";

    public static final String INVEST_ADVISE_FEE_TITLE = "投顾服务费";

    public static final String TRADE_FEE_TITLE = "转入、转出费用";

    public static final String TRADE_FEE_DESC = "以基金实际发生的申购、赎回费为准";

    public static final String PURCHASE_RULE_TITLE = "转入规则";

    public static final String REDEEM_RULE_TITLE = "转出规则";

    public static final String CHANGE_POSITION_RULE_TITLE = "调仓规则";

    public static final String FEE_DESC_TITLE = "费用说明";

    public static final String SPLIT_COMMA = ",";

    /**
     * 投顾服务费
     */
    public static final String ADVISE_FEE_TEXT_1 = "%s/年，由投顾机构收取；按持仓金额每天计算，每自然季度末或转出时收取。";

    /**
     * 投顾服务费
     */
    public static final String ADVISE_FEE_TEXT_2 = "投顾服务费为%s/年，由投顾机构收取；按持仓金额每天计算，每自然季度末或转出时收取。";

    /**
     * 目标盈投顾服务费
     */
    public static final String TARGET_ADVISE_FEE =
            "1 投顾服务费由投顾机构按持仓金额每天计算，每自然季度末或转出时收取。\n" +
            "2 若投资者在目标盈策略运作期主动退出，在退出时按照约定的投顾服务费率收取；若收益达到止盈条件自动退出，按照约定的投顾服务费率收取；若未触发止盈到期，按约定投顾服务费率或投顾机构提供的优惠费率收取。";

    /**
     * 基金交易费用
     */
    public static final String FUND_TRADE_FEE_DESC = "本服务在申购、赎回基金时，会按各只基金费率规则收取相关交易费用。基金交易产生的申购费、赎回费、转换费及其他基金销售费用由投资者承担。其中每只基金管理费、托管费、销售服务费将由基金公司从基金资产中每日计提，无需投资者在每笔交易中另行支付。基金投顾机构与投资者应根据法律法规及其他规定各自承担相应税费。";

    /**
     * 转入规则详情
     */
    public static final String PURCHASE_RULE_DETAIL =
            "1 在使用策略配比申购基金时，各只基金的配置比例均按策略确定；\n" +
                    "2 投资者在申购策略下的基金时，将为投资者在相应基金产品的注册登记机构开立基金账户；\n" +
                    "3 每个交易日9:30-15:00开放转入，投资者在该交易时间区间转入资金，将按照策略中各基金该交易日净值确认份额（QDII基金除外），其余时间不能转入资金；\n" +
                    "4 按照策略申购基金时，交易费用是策略项下单基金申购费用的加和；单一基金的申购费用以该基金在方正证券平台页面展示的为准；\n" +
                    "5 投资者是否能够成功按照策略配比申购各只基金，须以基金公司的确认信息为准。有可能由于基金账户开户失败、或基金公司确认失败等原因造成组合中单只或多只产品的申购失败。";

    /**
     * 目标盈转入规则详情
     */
    public static final String TARGET_PURCHASE_RULE_DETAIL =
            "1 目标盈策略参与期间，投资者可以随时转入资金；其余期间不能转入资金；\n" +
                    "2 在参与期结束后，由投顾机构统一按策略配比申购成分基金。在申购策略成分基金时，将为投资者在相应基金产品的注册登记机构开立基金账户；\n" +
                    "3 按照策略申购基金时，交易费用是策略项下单基金申购费用的加和；单一基金的申购费用以该基金在方正证券平台页面展示的为准；\n" +
                    "4 投资者是否能够成功按照策略配比申购各只基金，须以基金公司的确认信息为准。有可能由于基金账户开户失败、或基金公司确认失败等原因造成组合中单只或多只产品的申购失败。";


    /**
     * 转出规则详情
     */
    public static final String REDEEM_RULE_DETAIL =
            "1 资金转入后待基金确认后的下一个交易日投资者可以申请资金转出；\n" +
                    "2 资金转出时，投资者可以申请部分转出或全部转出，以百分比输入转出指令，若投资者当日账户中可赎回资产低于最低保有金额时，则只能一次性转出；\n" +
                    "3 每个交易日9:30-15:00开放转出，投资者在该交易时间区间转出资金，将按照策略中各基金该交易日净值确认份额（QDII基金除外），其余时间不能转出资金；\n" +
                    "4 按照策略赎回基金时，交易费用是策略项下单基金赎回费用的加和；若赎回的非货币基金持有时长少于7日（不含），基金公司将收取不低于1.5%的赎回费；单一基金的具体赎回费率以基金公司确认为准；\n" +
                    "5 投资者是否能够成功赎回各只基金，须以基金公司的确认信息为准；有可能由于基金公司确认失败等原因造成组合中单只或多只产品的赎回失败。";

    /**
     * 目标盈转出规则详情
     */
    public static final String TARGET_REDEEM_RULE_DETAIL =
            "1 参与期投资者可以随时撤单，无交易费用；\n" +
                    "2 观察期份额确认后、止盈运作期，投资者可以申请全部转出（调仓期间除外），将按照策略中各成分基金该交易日净值确认份额（QDII基金除外）；\n" +
                    "3 观察期、止盈运作期按照策略赎回基金时，交易费用是策略项下单基金赎回费用的加和；若赎回的非货币基金持有时长少于7日（不含），基金公司将收取不低于1.5%的赎回费；单一基金的具体赎回费率以基金公司确认为准；\n" +
                    "4 投资者是否能够成功赎回各只基金，须以基金公司的确认信息为准；有可能由于基金公司确认失败等原因造成组合中单只或多只产品的赎回失败。";




    /**
     * 调仓规则
     */
    public static final String CHANGE_POS_DESC = "基金投顾机构根据市场变化、基金动态研究成果、基金状态变更、组合策略优化与再平衡需要等情况，对授权账户内的基金投资组合策略生成调整方案，并根据调整后的方案发起对应的调仓交易。\n" +
            "若授权账户近期发生过交易，为避免因短期赎回费等原因导致投资者交易成本较高，基金投顾机构可通过延迟执行等方式优化调仓交易。";


    /**
     * 目标盈调仓规则
     */
    public static final String TARGET_CHANGE_POS_DESC =
            "1 基金投顾机构根据市场变化、基金动态研究成果、基金状态变更、组合策略优化与再平衡需要等情况，对授权账户内的基金投资组合策略生成调整方案，并根据调整后的方案发起对应的调仓交易。\n" +
            "2 若授权账户近期发生过交易，为避免因短期赎回费等原因导致投资者交易成本较高，基金投顾机构可通过延迟执行等方式优化调仓交易。";


    /**
     * 基金详情跳转地址
     */
    public static final String FUND_DETAIL_URL = "fzzqxf:///fund/detail/?fundCode=%s";

    /**
     * 默认交易结束时间
     */
    public static final Integer DEFAULT_TRADE_END_TIME = 150000;

    /**
     * 默认结束时间
     */
    public static final Integer DEFAULT_END_TIME = 235959;
}
