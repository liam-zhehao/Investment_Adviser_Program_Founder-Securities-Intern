package com.foundersc.ifte.invest.adviser.dubbo.service.impl.remote;

import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.dubbo.api.model.TradeTimeCheckRes;
import com.foundersc.ifte.invest.adviser.dubbo.api.service.TradeTimeRemoteService;
import com.foundersc.ifte.invest.adviser.dubbo.service.impl.support.TradeCalendarRemoteServiceAdapter;
import com.foundersc.ifte.invest.adviser.dubbo.util.AppVersionUtil;
import com.foundersc.ifte.invest.adviser.dubbo.util.DateUtil;
import com.foundersc.ifte.invest.adviser.dubbo.util.EnvUtil;
import com.foundersc.itc.product.model.TradeCalendar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wangfuwei
 * @date 2023/10/24
 */
@Service("tradeTimeRemoteService")
@Slf4j
public class TradeTimeRemoteServiceImpl implements TradeTimeRemoteService {
    @Autowired
    private TradeCalendarRemoteServiceAdapter tradeCalendarRemoteServiceAdapter;

    /**
     * 是否支持7*24
     */
    @Value("${support_7_24}")
    private boolean isSupport_7_24;

    /**
     * 支持7*24的最低版本
     */
    @Value("${lowest_version_7_24}")
    private String lowest_version_7_24;

    /**
     * 7*24白名单
     */
    @Value("${white_list_7_24}")
    private String white_list_7_24;

    /**
     * 交易起始时间默认为9:30
     */
    private final static int DEFAULT_TRADE_START_TIME = 90000;

    /**
     * 交易结束时间默认为15:00
     */
    private final static int DEFAULT_TRADE_END_TIME = 145958;

    /**
     * app版本大于等于8.30.0才支持7*24
     */
    private static AppVersionUtil.VersionRule versionRule;

    public static final String ALLOW_TRADE_TIME = "当前处于非交易时间，\n请在交易日9:00-15:00再来";

    private List<String> whiteList_7_24 = new ArrayList<>();

    @PostConstruct
    public void init() {
        log.info("isSupport_7_24={}, lowest_version_7_24={}", isSupport_7_24, lowest_version_7_24);
        versionRule = AppVersionUtil.greaterThanOrEqualToVersion(lowest_version_7_24);
        if (StringUtils.isNotBlank(white_list_7_24)) {
            whiteList_7_24 = Arrays.asList(StringUtils.split(white_list_7_24, ","));
            log.info("whiteList_7_24={}", whiteList_7_24);
        }
    }

    /**
     * 当前是否处于交易时间
     *
     * @return
     */
    public boolean isInTradeTime() {
        return isInTradeTime(DEFAULT_TRADE_START_TIME, DEFAULT_TRADE_END_TIME);
    }

    /**
     * 是否处于交易时间
     *
     * @param tradeStartTime 交易开始时间
     * @param tradeEndTime   交易结束时间
     * @return
     */
    public boolean isInTradeTime(int tradeStartTime, int tradeEndTime) {
        LocalTime beginTime = DateUtil.intToLocalTime(tradeStartTime);
        LocalTime endTime = DateUtil.intToLocalTime(tradeEndTime);
        boolean inTradeTime = isInTradeTime(beginTime, endTime);
        log.info("check trade time, startTime={}, endTime={}, inTradeTime={}", tradeStartTime, tradeEndTime, inTradeTime);
        return inTradeTime;
    }

    /**
     * 是否在交易时间中
     *
     * @param tradeStartTime 交易开始时间
     * @param tradeEndTime   交易结束时间
     * @return
     */
    public boolean isInTradeTime(LocalTime tradeStartTime, LocalTime tradeEndTime) {
        if (EnvUtil.isPre()) {
            // pre环境不校验交易日
            log.info("pre env, in trade date");
        } else {
            // 非pre环境，判断是否为交易日
            TradeCalendar tradeCalendar = tradeCalendarRemoteServiceAdapter.queryTradeDate(new Date());
            if (!tradeCalendar.isOpenMarket()) {
                return false;
            }
        }
        LocalTime now = LocalTime.now();
        return (now.isAfter(tradeStartTime) || now.equals(tradeStartTime)) && now.isBefore(tradeEndTime);
    }

    @Override
    public TradeTimeCheckRes checkTradeTime(String appVersion) {
        TradeTimeCheckRes res = new TradeTimeCheckRes();
        res.setInTradeTime(isInTradeTime());
        // 使用app进行版本控制
        res.setSupport_7_24(isSupport_7_24 && AppVersionUtil.isVersionMatch(versionRule, appVersion));
        if (!res.isInTradeTime()) {
            res.setTip(ALLOW_TRADE_TIME);
        }
        return res;
    }

    @Override
    public TradeTimeCheckRes checkTradeTime(SimpleAccount simpleAccount, String appVersion) {
        if (simpleAccount == null) {
            log.warn("simpleAccount is null, check trade time with appVersion");
            return checkTradeTime(appVersion);
        }
        TradeTimeCheckRes res = new TradeTimeCheckRes();
        res.setInTradeTime(isInTradeTime());
        // 使用app进行版本控制
        res.setSupport_7_24(judgeSupport_7_24(simpleAccount.getClientId(), appVersion));
        if (!res.isInTradeTime()) {
            res.setTip(ALLOW_TRADE_TIME);
        }
        return res;
    }

    /**
     * 判断是否支持7*24
     *
     * @param clientId
     * @param appVersion
     * @return
     */
    private boolean judgeSupport_7_24(String clientId, String appVersion) {
        if (!AppVersionUtil.isVersionMatch(versionRule, appVersion)) {
            return false;
        }
        return isSupport_7_24 || whiteList_7_24.contains(clientId);
    }
}
