package com.foundersc.ifte.invest.adviser.web.config;

import com.foundersc.ifte.invest.adviser.web.config.support.NotLocalCondition;
import com.foundersc.ifte.invest.adviser.web.util.EnvUtil;
import com.foundersc.itc.gateway.lib.config.ApiGatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 接入统一网关
 */
@Configuration
@Slf4j
@ConditionalOnProperty(value = "register-api-gateway", havingValue = "true")
public class GatewayConfig {
    /**
     * 本地启动不注入统一网关
     *
     * @return
     */
    @Bean
    @Conditional(NotLocalCondition.class)
    public ApiGatewayConfig apiGatewayConfig() {
        log.info("env: {}, inject ApiGatewayConfig", EnvUtil.getEnv());
        return new ApiGatewayConfig();
    }
}
