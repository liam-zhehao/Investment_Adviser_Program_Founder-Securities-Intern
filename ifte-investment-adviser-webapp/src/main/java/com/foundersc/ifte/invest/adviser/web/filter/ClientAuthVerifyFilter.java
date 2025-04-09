package com.foundersc.ifte.invest.adviser.web.filter;

import com.alibaba.fastjson.JSON;
import com.foundersc.ifc.auth.service.AuthAccountService;
import com.foundersc.ifc.t2.common.model.base.SimpleAccount;
import com.foundersc.ifte.invest.adviser.web.util.BeanUtil;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import com.foundersc.ifte.invest.adviser.web.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;

import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.CLIENT_AUTH_TOKEN_KEY;
import static com.foundersc.ifte.invest.adviser.web.constants.CommonConstants.W_GATEWAY_CLIENT;

/**
 * 客户号登录验证
 */
@Slf4j
@WebFilter(filterName = "clientAuthVerifyFilter", urlPatterns = {"/api/investAdviser/clientAuth/*"})
public class ClientAuthVerifyFilter extends OncePerRequestFilter {

    @DubboReference
    private AuthAccountService authAccountService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.info("client auth verify, token={}", httpServletRequest.getHeader(CLIENT_AUTH_TOKEN_KEY));
        // 本地时，使用W-GATEWAY-CLIENT验证客户号登录方便测试
        if (EnvUtil.isLocal() && !setSimpleAccount(httpServletRequest)) {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        } else if (!EnvUtil.isLocal() && !verifyClientAuthToken(httpServletRequest)) {
            // 客户号登录验证
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
        ContextHolder.removeSimpleAccount();
    }

    /**
     * 填充缺少的信息，比如密码，资金账号
     *
     * @param simpleAccount
     */
    private void fillLackInfo(SimpleAccount simpleAccount) {
        // 首先调用auth的dubbo服务查询
        com.foundersc.ifc.hang.model.base.SimpleAccount authSimpleAccount = authAccountService.getSimpleAccountByClientId(simpleAccount.getClientId());
        if (!Objects.isNull(authSimpleAccount)) {
            log.info("clientId={}, fill pwd from authSimpleAccount", simpleAccount.getClientId());
            simpleAccount.setPassword(authSimpleAccount.getPassword());
            if (StringUtils.isBlank(simpleAccount.getFundAccount())) {
                log.info("clientId={}, fill fundAccount from authSimpleAccount: {}", simpleAccount.getClientId(), authSimpleAccount.getFundAccount());
                simpleAccount.setFundAccount(authSimpleAccount.getFundAccount());
            }
        }
    }

    /**
     * 验证客户号登录token
     *
     * @param httpServletRequest
     * @return
     */
    private boolean verifyClientAuthToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader(CLIENT_AUTH_TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            log.warn("verifyClientAuthToken fail, token is blank");
            return false;
        }
        com.foundersc.ifc.hang.model.base.SimpleAccount hangSimpleAccount = authAccountService.getSimpleAccountByToken(token);
        if (hangSimpleAccount == null || StringUtils.isBlank(hangSimpleAccount.getClientId())) {
            log.warn("verifyClientAuthToken fail, token is not valid, token={}, hangSimpleAccount={}", token, hangSimpleAccount);
            return false;
        }
        SimpleAccount simpleAccount = new SimpleAccount();
        BeanUtil.copyProperties(hangSimpleAccount, simpleAccount);
        if (StringUtils.isBlank(simpleAccount.getInvestorAccount())) {
            log.info("investorAccount is blank, set it with fundAccount={}", simpleAccount.getFundAccount());
            simpleAccount.setInvestorAccount(simpleAccount.getFundAccount());
        }
        log.info("bind simpleAccount: {}", simpleAccount);
        ContextHolder.setSimpleAccount(simpleAccount);
        return true;
    }

    /**
     * 设置SimpleAccount（使用统一网关时用该方法）
     *
     * @param httpServletRequest
     * @return
     */
    private boolean setSimpleAccount(HttpServletRequest httpServletRequest) {
        String strGatewayClient = httpServletRequest.getHeader(W_GATEWAY_CLIENT);
        if (StringUtils.isNotBlank(strGatewayClient)) {
            try {
                strGatewayClient = URLDecoder.decode(strGatewayClient, "UTF-8");
                SimpleAccount simpleAccount = JSON.parseObject(strGatewayClient, SimpleAccount.class);
                // todo: 确认investorAccount是否有值
                if (StringUtils.isBlank(simpleAccount.getInvestorAccount())) {
                    log.info("investorAccount is blank, set it with fundAccount={}", simpleAccount.getFundAccount());
                    simpleAccount.setInvestorAccount(simpleAccount.getFundAccount());
                }
                log.info("bind simpleAccount: {}", simpleAccount);
                fillLackInfo(simpleAccount);
                ContextHolder.setSimpleAccount(simpleAccount);
                return true;
            } catch (Exception e) {
                log.error("解析SimpleAccount异常，str={}", strGatewayClient, e);
                return false;
            }
        }
        log.error("设置SimpleAccount失败，{}为空", W_GATEWAY_CLIENT);
        return false;
    }
}
