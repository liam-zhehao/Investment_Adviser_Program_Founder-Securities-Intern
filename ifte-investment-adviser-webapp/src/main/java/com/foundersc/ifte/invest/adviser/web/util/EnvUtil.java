package com.foundersc.ifte.invest.adviser.web.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 环境相关
 */
@Component
@Slf4j
public class EnvUtil {
    private EnvUtil() {
    }

    private static String env;

    private static Environment environment;

    @Value("${spring.profiles.active}")
    public void setEnv(String env) {
        EnvUtil.env = env;
    }

    public static void setEnvironment(Environment environmentVal) {
        if (environmentVal == null) {
            return;
        }
        EnvUtil.environment = environmentVal;
    }

    /**
     * 本地环境
     */
    private static final String LOCAL_ENV = "local";

    /**
     * dev环境
     */
    private static final String DEV_ENV = "dev";

    /**
     * qa环境
     */
    private static final String QA_ENV = "qa";

    /**
     * 全网测试环境
     */
    private static final String PRE_ENV = "pre";

    /**
     * prod环境
     */
    private static final String PROD_ENV = "product";

    /**
     * 返回当前环境
     *
     * @return
     */
    public static String getEnv() {
        if (StringUtils.isBlank(env) && environment != null) {
            env = environment.getProperty("spring.profiles.active");
            log.info("get env from environment: {}", env);
        }
        log.info("env: {}", env);
        return env;
    }

    /**
     * 是否为local环境
     *
     * @return
     */
    public static boolean isLocal() {
        return LOCAL_ENV.equalsIgnoreCase(getEnv());
    }

    public static boolean isLocal(Environment environmentVal) {
        setEnvironment(environmentVal);
        return isLocal();
    }

    /**
     * 是否为dev环境
     *
     * @return
     */
    public static boolean isDev() {
        return DEV_ENV.equalsIgnoreCase(getEnv());
    }

    public static boolean isDev(Environment environmentVal) {
        setEnvironment(environmentVal);
        return isDev();
    }

    /**
     * 是否qa环境
     *
     * @return
     */
    public static boolean isQa() {
        return QA_ENV.equalsIgnoreCase(getEnv());
    }

    public static boolean isQa(Environment environmentVal) {
        setEnvironment(environmentVal);
        return isQa();
    }

    /**
     * 是否全网测试环境
     *
     * @return
     */
    public static boolean isPre() {
        return PRE_ENV.equalsIgnoreCase(getEnv());
    }

    public static boolean isPre(Environment environmentVal) {
        setEnvironment(environmentVal);
        return isPre();
    }

    /**
     * 是否生产环境
     *
     * @return
     */
    public static boolean isProd() {
        return PROD_ENV.equalsIgnoreCase(getEnv());
    }

    public static boolean isProd(Environment environmentVal) {
        setEnvironment(environmentVal);
        return isProd();
    }
}
