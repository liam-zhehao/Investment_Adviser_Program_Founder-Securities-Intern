package com.foundersc.ifte.invest.adviser.web.common.async;

import com.foundersc.ifte.invest.adviser.web.util.MdcUtil;

import java.util.concurrent.Callable;

/**
 * 为异步任务执行时增加traceId
 */
public abstract class TraceableCallable<V> implements Callable<V> {
    private String parentTraceId;

    public TraceableCallable() {
        parentTraceId = MdcUtil.getTraceId();
    }

    @Override
    public V call() throws Exception {
        try {
            MdcUtil.setTraceId(parentTraceId);
            return doCall();
        } finally {
            MdcUtil.removeTraceId();
        }
    }

    /**
     * 真正执行call方法
     *
     * @return
     * @throws Exception
     */
    protected abstract V doCall() throws Exception;
}
