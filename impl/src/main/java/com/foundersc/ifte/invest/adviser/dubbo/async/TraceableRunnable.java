package com.foundersc.ifte.invest.adviser.dubbo.async;

import com.foundersc.ifte.invest.adviser.dubbo.util.MdcUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 为异步任务执行时增加traceId
 *
 * @author wangfuwei
 * @date 2023/9/6
 */
@Slf4j
public abstract class TraceableRunnable implements Runnable {
    private String parentTraceId;

    public TraceableRunnable() {
        parentTraceId = MdcUtil.getTraceId();
    }

    @Override
    public final void run() {
        try {
            MdcUtil.setTraceId(parentTraceId);
            doRun();
        } finally {
            // 线程池时，如果拒绝策略为CallerRunsPolicy，重新置为父线程的traceId
            MdcUtil.removeTraceId();
        }
    }

    /**
     * 真正执行run方法
     */
    protected abstract void doRun();
}
