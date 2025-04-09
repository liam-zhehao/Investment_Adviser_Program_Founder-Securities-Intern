package com.foundersc.ifte.invest.adviser.web.aspect;

import com.alibaba.fastjson.JSON;
import com.foundersc.ifte.invest.adviser.web.model.Response;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.ifte.invest.adviser.web.util.MdcUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * web请求日志记录
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {
    /**
     * 忽略打印结果的uri名称，result太长且跟客户无关
     */
    private static final Set<String> ignorePrintResultUriSet = new HashSet<>();

    static {
        ignorePrintResultUriSet.add("/api/investAdviser/clientAuth/combine/info");
        ignorePrintResultUriSet.add("/api/investAdviser/clientAuth/combine/trend");
        ignorePrintResultUriSet.add("/api/investAdviser/clientAuth/combine/position");
        ignorePrintResultUriSet.add("/api/investAdviser/clientAuth/combine/tradeRule");
        ignorePrintResultUriSet.add("/api/investAdviser/clientAuth/trade/epaper/list");
        ignorePrintResultUriSet.add("/api/investAdviser/noAuth/homePage");
        ignorePrintResultUriSet.add("/api/investAdviser/clientAuth/kyc/paper");
        ignorePrintResultUriSet.add("/api/investAdviser/clientAuth/kyc/investDemand");
    }

    /**
     * 日志记录请求uri，参数，响应结果及耗时等
     *
     * @param jp
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.foundersc.ifte.invest.adviser.web.controller..*.*(..))")
    public Object process(ProceedingJoinPoint jp) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        // 增加用户信息相关
        log.info("simpleAccount: {}, mobile: {}, request uri: {}, args: {}", ContextHolder.getSimpleAccount(), ContextHolder.getMobileLoginInfo(), uri, jp.getArgs());
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = jp.proceed();
            return result;
        } catch (Throwable e) {
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - start;
            if (result instanceof Response) {
                ((Response<?>) result).setCost(cost);
                ((Response<?>) result).setTraceId(MdcUtil.getTraceId());
            }
            if (ignorePrintResultUriSet.contains(uri)) {
                result = "[ignore result]";
            }
            log.info("uri: {} cost {}ms and result {}", uri, cost, JSON.toJSONString(result));
        }
    }
}
