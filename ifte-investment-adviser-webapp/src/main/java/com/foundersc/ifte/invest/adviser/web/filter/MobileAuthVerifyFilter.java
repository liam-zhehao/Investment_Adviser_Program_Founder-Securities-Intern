package com.foundersc.ifte.invest.adviser.web.filter;

import com.foundersc.ifc.user.model.UserInfo;
import com.foundersc.ifc.user.service.UserService;
import com.foundersc.ifte.invest.adviser.web.constants.CommonConstants;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.ifte.invest.adviser.web.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 手机号登录验证
 */
@Slf4j
//@WebFilter(filterName = "mobileAuthVerifyFilter", urlPatterns = {"/api/investAdviser/mobileAuth/*", "/api/investAdviser/clientAuth/*"})
public class MobileAuthVerifyFilter extends OncePerRequestFilter {

    @DubboReference
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 本地环境时，暂不验证手机号登录，方便测试
        if (!EnvUtil.isLocal() && !verifyMobileToken(httpServletRequest)) {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        ContextHolder.removeMobileLoginInfo();
    }

    /**
     * 验证手机号登录
     *
     * @param httpServletRequest
     * @return
     */
    private boolean verifyMobileToken(HttpServletRequest httpServletRequest) {
        String uToken = httpServletRequest.getHeader(CommonConstants.MOBILE_AUTH_TOKEN_KEY);
        if (StringUtils.isBlank(uToken)) {
            log.warn("verifyMobileToken fail, token is blank");
            return false;
        }
        UserInfo userInfo = userService.newVerifyToken(uToken);
        log.info("verifyMobileToken, uToken={}, userInfo={}", uToken, userInfo);
        if (userInfo == null || StringUtils.isBlank(userInfo.getMobile())) {
            log.warn("verifyMobileToken fail");
            return false;
        }
        ContextHolder.MobileLoginInfo mobileLoginInfo = new ContextHolder.MobileLoginInfo(userInfo.getUid(), userInfo.getMobile());
        log.info("bind mobileLoginInfo={}", mobileLoginInfo);
        ContextHolder.setMobileLoginInfo(mobileLoginInfo);
        return true;
    }
}
