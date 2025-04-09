package com.foundersc.ifte.invest.adviser.dubbo.async;

import com.foundersc.ifte.invest.adviser.dubbo.util.MdcUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 为异步任务执行时增加traceId
 *
 * @author wangfuwei
 * @date 2023/9/6
 */
@Slf4j
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
            // 线程池时，如果拒绝策略为CallerRunsPolicy，重新置为父线程的traceId
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
