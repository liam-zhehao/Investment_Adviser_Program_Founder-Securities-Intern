package com.foundersc.ifte.invest.adviser.dubbo.service.impl.support;

import com.foundersc.ifc.portfolio.t2.enums.v2.comb.CombBusinTypeEnum;
import com.foundersc.ifc.portfolio.t2.request.v2.comb.QueryTradeDateRuleReq;
import com.foundersc.ifc.portfolio.t2.response.v2.comb.QueryTradeDateRuleResp;
import com.foundersc.ifc.portfolio.t2.service.v2.CombineService;
import com.foundersc.ifc.t2.common.model.base.BaseResult;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.comb.TradeRule;
import com.foundersc.ifte.invest.adviser.dubbo.util.DateUtil;
import com.foundersc.itc.product.service.TradeCalendarRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum.QUERY_TRADE_DATE_RULE_ERROR;
import static com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants.TODAY;
import static com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants.VIEW_PROFIT_DATE;

/**
 * 交易时间轴
 *
 * @author wangfuwei
 * @date 2022/9/27
 */
@Component
@Slf4j
public class TradeTimeline {
    private static final String TIMELINE_BUY = "交易受理";

    private static final String BUY_CONFIRM = "确认份额";

    private static final String VIEW_PROFIT = "查看盈亏";

    private static final String TIMELINE_REDEEM = "转出";

    private static final String REDEEM_CONFIRM = "确认金额";

    private static final String FUND_ARRIVE = "到账保证金";

    private static final String BEFORE_DEAD_TIME = "15点前";

    private static final String AFTER_DEAD_TIME = "15点后";

    /**
     * 交易截止时间
     */
    private final static LocalTime tradeDeadTime = LocalTime.of(15, 0, 0);

    @Autowired
    private CombineService combineService;

    @Autowired
    private TradeCalendarRemoteService tradeCalendarRemoteService;

    /**
     * 卖出时间轴
     *
     * @param simpleAccount
     * @param combineCode
     * @param localDateTime 当前日期时间
     * @return
     */
    public List<TradeRule.TimelineItem> getRedeemTimeline(SimpleAccount simpleAccount, String combineCode, LocalDateTime localDateTime) {
        QueryTradeDateRuleReq queryTradeDateRuleReq = new QueryTradeDateRuleReq();
        queryTradeDateRuleReq.setCombineCode(combineCode);
        queryTradeDateRuleReq.setCombBusinType(CombBusinTypeEnum.REDUCE_INVEST);
        queryTradeDateRuleReq.setInitDate(getInitDate(localDateTime));
        BaseResult<QueryTradeDateRuleResp> baseResult = combineService.queryTradeDateRule(simpleAccount, queryTradeDateRuleReq);
        QueryTradeDateRuleResp tradeDateRuleResp = getTradeDateRuleResp(baseResult);
        List<TradeRule.TimelineItem> result = new ArrayList<>();

        result.add(new TradeRule.TimelineItem(TIMELINE_REDEEM, getTradeCopywriting(DateUtil.localDateTimeToInt(localDateTime), isAfterDeadTime(localDateTime))));
        result.add(new TradeRule.TimelineItem(REDEEM_CONFIRM, formatDateWithWeek(tradeDateRuleResp.getPre_affirm_date())));
        result.add(new TradeRule.TimelineItem(FUND_ARRIVE, formatDateWithWeek(getPreArriveDate(tradeDateRuleResp.getPre_arrive_date()))));
        return result;
    }

    /**
     * pre_arrive_date是到投顾资金的预计日期，预计到账日期取pre_arrive_date的下一个交易日
     *
     * @param intPreArriveDate
     * @return
     */
    private int getPreArriveDate(int intPreArriveDate) {
        LocalDate preArriveLocaleDate = DateUtil.intToDate(intPreArriveDate)
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate nextLocalDate = preArriveLocaleDate.plusDays(1);
        return tradeCalendarRemoteService.queryNextTradeDate(DateUtil.localDateToDate(nextLocalDate));
    }


    /**
     * 买入时间轴
     *
     * @param simpleAccount
     * @param combineCode
     * @param localDateTime 当前日期时间
     * @return
     */
    public List<TradeRule.TimelineItem> getBuyTimeline(SimpleAccount simpleAccount, String combineCode, LocalDateTime localDateTime) {
        QueryTradeDateRuleReq queryTradeDateRuleReq = new QueryTradeDateRuleReq();
        queryTradeDateRuleReq.setCombineCode(combineCode);
        queryTradeDateRuleReq.setCombBusinType(CombBusinTypeEnum.ADD_INVEST);
        queryTradeDateRuleReq.setInitDate(getInitDate(localDateTime));
        BaseResult<QueryTradeDateRuleResp> baseResult = combineService.queryTradeDateRule(simpleAccount, queryTradeDateRuleReq);
        QueryTradeDateRuleResp tradeDateRuleResp = getTradeDateRuleResp(baseResult);
        List<TradeRule.TimelineItem> result = new ArrayList<>();

        result.add(new TradeRule.TimelineItem(TIMELINE_BUY, getTradeCopywriting(DateUtil.localDateTimeToInt(localDateTime), isAfterDeadTime(localDateTime))));
        result.add(new TradeRule.TimelineItem(BUY_CONFIRM, formatDateWithWeek(tradeDateRuleResp.getPre_affirm_date())));
        result.add(new TradeRule.TimelineItem(VIEW_PROFIT, String.format(VIEW_PROFIT_DATE, DateUtil.formatMonthDay(tradeDateRuleResp.getPre_income_date()))));
        return result;
    }

    /**
     * 交易文案：例如 今日15点前
     *
     * @param today
     * @param isAfterDeadTime
     * @return
     */
    private String getTradeCopywriting(int today, boolean isAfterDeadTime) {
        return String.format("%s%s", TODAY, isAfterDeadTime ? AFTER_DEAD_TIME : BEFORE_DEAD_TIME);
        // return String.format("%s%s", TODAY, BEFORE_DEAD_TIME);
    }

    /**
     * 格式化日期： 08-12（星期二）
     *
     * @param intDate
     * @return
     */
    private String formatDateWithWeek(int intDate) {
        Date date = DateUtil.intToDate(intDate);
        return String.format("%s（%s）", DateUtil.formatMonthDay(intDate), cn.hutool.core.date.DateUtil.dayOfWeekEnum(date).toChinese());
    }

    /**
     * 是否为15:00之后
     *
     * @return
     */
    private boolean isAfterDeadTime(LocalDateTime localDateTime) {
        return LocalTime.from(localDateTime).isAfter(tradeDeadTime);
    }


    /**
     * localDateTime，15:00之前传当天，15:00之后传第二天
     *
     * @param localDateTime 当前日期时间
     * @return
     */
    private Integer getInitDate(LocalDateTime localDateTime) {
        if (isAfterDeadTime(localDateTime)) {
            // 15:00之后取下一个交易日（不含当天）
            LocalDateTime nextLocalDateTime = localDateTime.plusDays(1);
            Date date = Date.from(nextLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            return tradeCalendarRemoteService.queryNextTradeDate(date);
        } else {
            // 15:00之前取下一个交易日（含当天）
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            return tradeCalendarRemoteService.queryNextTradeDate(date);
        }
    }

    private QueryTradeDateRuleResp getTradeDateRuleResp(BaseResult<QueryTradeDateRuleResp> baseResult) {
        if (!baseResult.isSuccess()) {
            log.error(QUERY_TRADE_DATE_RULE_ERROR + "queryTradeDateRule t2 failed, code={}, errorMsg={}", baseResult.getCode(), baseResult.getErrorMsg());
            throw new BusinessException(QUERY_TRADE_DATE_RULE_ERROR);
        }
        if (baseResult.getData() == null) {
            log.error(QUERY_TRADE_DATE_RULE_ERROR + "queryTradeDateRule failed, data is null");
            throw new BusinessException(QUERY_TRADE_DATE_RULE_ERROR);
        }
        if (!baseResult.isSuccess()) {
            log.error(QUERY_TRADE_DATE_RULE_ERROR + "queryTradeDateRule failed, error_no={}, error_info={}", baseResult.getData().getError_no(), baseResult.getData().getError_info());
            throw new BusinessException(QUERY_TRADE_DATE_RULE_ERROR);
        }
        return baseResult.getData();
    }
}
