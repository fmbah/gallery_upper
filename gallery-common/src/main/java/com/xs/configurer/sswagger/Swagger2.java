package com.xs.configurer.sswagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.xs.core.ProjectConstant.BACK_MANAGER_MENUID;

/**
 * @Auther: Fmbah
 * @Date: 18-10-10 下午4:24
 * @Description: swagger 配置
 */
@Configuration
@EnableSwagger2
public class Swagger2 {

    @Value("${spring.profiles.active}")
    private String env;
    @Bean
    public Docket api() {
        //添加head参数
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name(BACK_MANAGER_MENUID).description("菜单id").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xs.controllers"))
                .build()
                .globalOperationParameters(pars).enable(!"dev".equals(env) ? false : true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("提供给前端开发人员的后台程序api文档")
//                .description("官网地址 http://xxx.xxx.xxx")
//                .termsOfServiceUrl("http://xxx.xxx.xxx")
//                .contact("官网地址")
                .version("1.0")
                .build();
    }
}