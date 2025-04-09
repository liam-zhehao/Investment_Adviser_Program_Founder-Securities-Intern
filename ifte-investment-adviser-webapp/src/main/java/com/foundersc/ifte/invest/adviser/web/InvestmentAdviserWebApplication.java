package com.foundersc.ifte.invest.adviser.web;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * @description Springboot启动类
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableDubbo(scanBasePackages = "com.foundersc.ifte.invest.adviser.web")
@ImportResource({"classpath*:dubbo_config/*.xml"})
@ServletComponentScan
public class InvestmentAdviserWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestmentAdviserWebApplication.class, args);
    }

}
