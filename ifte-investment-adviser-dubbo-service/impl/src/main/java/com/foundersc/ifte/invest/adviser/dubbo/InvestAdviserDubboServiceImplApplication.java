package com.foundersc.ifte.invest.adviser.dubbo;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableDubbo(scanBasePackages = "com.foundersc.ifte.invest.adviser.dubbo")
@ImportResource({"classpath*:dubbo_config/*.xml"})
public class InvestAdviserDubboServiceImplApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(InvestAdviserDubboServiceImplApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
