package com.foundersc.ifte.invest.adviser.web.filter;

import com.foundersc.ifte.invest.adviser.web.util.MdcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 给日志增加traceId
 */
@Slf4j
public class TraceIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MdcUtil.setTraceId();
        filterChain.doFilter(request, response);
        MdcUtil.removeTraceId();
    }
}
