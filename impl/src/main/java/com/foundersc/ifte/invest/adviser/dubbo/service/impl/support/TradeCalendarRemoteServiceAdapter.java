package com.foundersc.ifte.invest.adviser.dubbo.service.impl.support;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import com.foundersc.ifc.common.util.DateUtils;
import com.foundersc.ifte.invest.adviser.dubbo.constant.CommonConstants;
import com.foundersc.itc.product.model.TradeCalendar;
import com.foundersc.itc.product.service.TradeCalendarRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author wangfuwei
 * @date 2023/9/21
 */
@Component
public class TradeCalendarRemoteServiceAdapter {
    /**
     * 指定时间及以后首个交易日的缓存
     */
    private final Cache<String, Integer> nextTradeDateCache = CacheUtil.newLRUCache(365, 24 * 60 * 60 * 1000);

    /**
     * 交易日信息缓存
     */
    private final Cache<String, TradeCalendar> tradeCalendarCache = CacheUtil.newLRUCache(365, 24 * 60 * 60 * 1000);

    @Autowired
    private TradeCalendarRemoteService tradeCalendarRemoteService;

    /**
     * 获取指定时间及以后的首个交易日
     *
     * @param startDate 开始日期（包括）
     * @return
     */
    public Integer queryNextTradeDate(Date startDate) {
        String strStartDate = dateToStr(startDate);
        if (nextTradeDateCache.containsKey(strStartDate)) {
            return nextTradeDateCache.get(strStartDate, true);
        }
        Integer result = tradeCalendarRemoteService.queryNextTradeDate(startDate);
        nextTradeDateCache.put(strStartDate, result);
        return result;
    }

    /**
     * Date转字符串yyyyMMdd
     *
     * @param date
     * @return
     */
    private String dateToStr(Date date) {
        return DateUtils.formatDate(date, CommonConstants.DEFAULT_DATE_FORMAT);
    }

    /**
     * 根据日期查询交易日信息
     *
     * @param date 自然日期
     * @return
     */
    public TradeCalendar queryTradeDate(Date date) {
        String strDate = dateToStr(date);
        if (tradeCalendarCache.containsKey(strDate)) {
            return tradeCalendarCache.get(strDate);
        }
        TradeCalendar tradeCalendar = tradeCalendarRemoteService.queryTradeDate(date);
        tradeCalendarCache.put(strDate, tradeCalendar);
        return tradeCalendar;
    }
}
