package com.foundersc.ifte.invest.adviser.web.util;

import org.springframework.util.StopWatch;

/**
 * @author wangfuwei
 * @date 2022/11/30
 */
public class LogUtil {
    private LogUtil() {
    }

    /**
     * StopWatch的打印信息
     *
     * @param stopWatch
     * @return
     */
    public static String stopWatchLog(StopWatch stopWatch) {
        return String.format(stopWatch.getLastTaskName() + " cost " + stopWatch.getLastTaskTimeMillis() + "ms");
    }
}
