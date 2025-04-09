package com.foundersc.ifte.invest.adviser.web.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * 设置日志的traceId工具类
 */
public class MdcUtil {
    public static final String TRACE_ID = "traceId";

    /**
     * 设置MDC的traceId
     */
    public static void setTraceId() {
        setTraceId(null);
    }

    /**
     * 使用指定前缀设置MDC的traceId
     *
     * @param prefix
     */
    public static void setTraceId(String prefix) {
        String traceId = UUID.randomUUID().toString().toLowerCase().replace("-", "");
        if (StringUtils.isNotBlank(prefix)) {
            traceId = StringUtils.joinWith("-", prefix, traceId);
        }
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 获取当前线程的traceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 移除traceId
     */
    public static void removeTraceId() {
        MDC.remove(TRACE_ID);
    }
}
