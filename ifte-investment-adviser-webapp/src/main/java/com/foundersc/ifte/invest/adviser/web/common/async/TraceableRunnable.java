package com.foundersc.ifte.invest.adviser.web.common.async;

import com.foundersc.ifte.invest.adviser.web.util.MdcUtil;

/**
 * 为异步任务执行时增加traceId
 */
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
            MdcUtil.removeTraceId();
        }
    }

    /**
     * 真正执行run方法
     */
    protected abstract void doRun();
}
