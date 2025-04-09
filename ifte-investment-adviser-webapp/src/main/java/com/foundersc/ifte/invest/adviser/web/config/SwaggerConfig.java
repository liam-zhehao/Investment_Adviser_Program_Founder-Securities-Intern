package com.foundersc.ifte.invest.adviser.web.config;

import com.foundersc.ifte.invest.adviser.web.constants.CommonConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 配置swagger注释
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    /**
     * 不使用swagger的环境
     */
    private final List<String> SWAGGER_DISABLED_ENVS = Arrays.asList("product");

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("invest-adviser")
                .apiInfo(new ApiInfoBuilder()
                        .title("投资顾问接口文档")
                        .description("投资顾问接口文档，以此文档为准")
                        .contact(new Contact("wangfuwei", "", "wangfuwei@foundersc.com")).version("1.0").build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.foundersc.ifte.invest.adviser.web.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(getParameterList())
                .enable(!SWAGGER_DISABLED_ENVS.contains(activeProfile));
    }

    /**
     * 添加head参数配置
     */
    private List<Parameter> getParameterList() {
        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder gatewayClient = new ParameterBuilder();
        gatewayClient.name(CommonConstants.W_GATEWAY_CLIENT).description("统一网关认证后的信息（测试用，根据需要修改）")
                .modelRef(new ModelRef("string"))
                .scalarExample("{\"branchNo\":2032,\"clientId\":\"2000020510\",\"corpRiskLevel\":5,\"fundAccount\":\"2000020510\",\"key\":\"simple_account_10010712\",\"mobile\":\"15507487211\",\"opStation\":\"MPN:15507487211;MAC:020000000000;IMEI:7F01CFF5-B509-4B50-9529-E8F3FE21432D;OSV:IOS;VER:7.21.0;MIP:10.150.225.0;REMARK:XF2\",\"password\":\"123123\",\"sysNo\":\"2\"}")
                .parameterType("header")
                .required(false).build();
        pars.add(gatewayClient.build());
        ParameterBuilder mobileToken = new ParameterBuilder();
        mobileToken.name(CommonConstants.MOBILE_AUTH_TOKEN_KEY).description("手机号登录成功后的token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false).build();
        pars.add(mobileToken.build());
        ParameterBuilder clientToken = new ParameterBuilder();
        clientToken.name(CommonConstants.CLIENT_AUTH_TOKEN_KEY).description("客户号登录token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false).build();
        pars.add(clientToken.build());
        return pars;
    }
}
