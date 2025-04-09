package com.foundersc.ifte.invest.adviser.dubbo.filter;

import com.foundersc.ifte.invest.adviser.dubbo.util.MdcUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * 日志记录
 */
@Slf4j
@Activate(group = CommonConstants.PROVIDER, order = -1)
public class LogProviderFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long startTime = System.currentTimeMillis();
        //RpcContext获取传递的TraceId
        String traceId = RpcContext.getContext().getAttachment(MdcUtil.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            MdcUtil.setTraceId();
        } else {
            MdcUtil.setTraceId(traceId);
        }
        log.info("dubbo入参，InterfaceName={}, MethodName={}, Parameter={}", invocation.getInvoker().getInterface().getName(), invocation.getMethodName(), invocation.getArguments());
        try {
            return invoker.invoke(invocation);
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            log.info("dubbo响应，InterfaceName={}, MethodName={}, SpendTime={} ms", invocation.getInvoker().getInterface().getName(), invocation.getMethodName(), cost);
            MdcUtil.removeTraceId();
        }
    }
}
