package com.foundersc.ifte.invest.adviser.dubbo.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InvestAdviserDubboServiceContext {
    private static final String[] CONFIG_FILES = new String[]{
            "classpath:dubbo_config/common.xml",
            "classpath:dubbo_config/common_consumer.xml",
            "classpath:dubbo_config/ifte-investment-adviser-dubbo-consumer.xml",
            "classpath*:dubbo_config/*-app.xml"
    };

    private static final InvestAdviserDubboServiceContext INSTANCE = new InvestAdviserDubboServiceContext();

    private final ClassPathXmlApplicationContext applicationContext;

    private InvestAdviserDubboServiceContext() {
        applicationContext = new ClassPathXmlApplicationContext(CONFIG_FILES);
        applicationContext.start();
    }

    public static InvestAdviserDubboServiceContext getInstance() {
        return INSTANCE;
    }
}
