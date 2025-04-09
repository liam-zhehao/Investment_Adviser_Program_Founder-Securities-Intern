package com.foundersc.ifte.invest.adviser.dubbo.filter;

/**
 * @author wangfuwei
 * @date 2022/11/7
 */

import com.foundersc.ifc.common.util.StringUtils;
import com.foundersc.ifte.invest.adviser.dubbo.util.MdcUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 调用dubbo服务时记录请求参数，结果，耗时等
 */
@Activate(group = CommonConstants.CONSUMER)
@Slf4j
public class DubboConsumerLogFilter implements Filter {
    /**
     * 忽略打印结果的dubbo服务及方法名称，result太长
     */
    private static final Map<String, Set<String>> ignorePrintResultMap = new HashMap<>();
    private static Set<String> ignoreServices = new HashSet<>();
    private static Set<String> deleteServices = new HashSet<>();
    private static Set<String> ignoreMethods = new HashSet<>();
    static {
        ignoreServices.add("com.foundersc.ifc.portfolio.t2.service.v2.EmployeeCombineService");
        deleteServices.add("com.foundersc.ifc.portfolio.t2.service.InvestAccountService");
        ignoreMethods.add("queryCombHisPrice");

    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long start = System.currentTimeMillis();
        String traceId = MdcUtil.getTraceId();
        if (StringUtils.isBlank(traceId)) {
            MdcUtil.setTraceId();
        }
        Result result = null;
        String interfaceName = invocation.getInvoker().getInterface().getName();
        String methodName = invocation.getMethodName();
        try {
            RpcContext.getContext().setAttachment(MdcUtil.TRACE_ID, MdcUtil.getTraceId());
            result = invoker.invoke(invocation);
            if (result.hasException()) {
                log.error("dubbo调用异常，InterfaceName={}, MethodName={}, Parameter={}, exception={}", invocation.getInvoker().getInterface().getName(), invocation.getMethodName(), invocation.getArguments(), result.getException());
            }
            return result;
        } catch (RpcException e) {
            if(!shouldIgnoreResult(interfaceName, methodName)) {
                log.error("dubbo调用异常【RpcException】，InterfaceName={}, MethodName={}, Parameter={}", invocation.getInvoker().getInterface().getName(), invocation.getMethodName(), invocation.getArguments(), e);
            }
            throw e;
        } finally {
            Object resultValue = shouldIgnoreResult(interfaceName, methodName) ? "[ignore result]" : (result != null ? result.getValue() : null);
            long cost = System.currentTimeMillis() - start;
            if (!shouldDeleteResult(interfaceName, methodName) && !Thread.currentThread().getName().startsWith("DubboMonitorSendTimer")) {
                log.info("dubbo响应，InterfaceName={}, MethodName={}, Parameter={}, SpendTime={} ms, result={}", interfaceName, methodName, invocation.getArguments(), cost, resultValue);
            }
        }
    }

    /**
     * 是否要忽略掉result的打印
     *
     * @param interfaceName
     * @param methodName
     * @return
     */
    private boolean shouldIgnoreResult(String interfaceName, String methodName) {
        return ignoreServices.contains(interfaceName) || ignoreMethods.contains(methodName);
    }

    private boolean shouldDeleteResult(String interfaceName, String methodName) {
        return deleteServices.contains(interfaceName);
    }
}
