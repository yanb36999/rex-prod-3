package com.zmcsoft.rex.starter.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hswebframework.web.authorization.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口文档配置,只会在开发和测试环境生效
 *
 * @author zhouhao
 * @since 1.0
 */
@Configuration
@Profile({"dev", "test","prod"})
@EnableSwagger2
public class ApiDocConfiguration {
    //    @Bean
    public Docket hswebApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("hsweb 3.0 api")
                        .description("hsweb 企业后台管理基础框架")
                        .termsOfServiceUrl("http://www.hsweb.io/")
                        .license("apache 2.0")
                        .version("3.0")
                        .build())
                .groupName("hsweb")
                .ignoredParameterTypes(HttpSession.class, Authentication.class, HttpServletRequest.class, HttpServletResponse.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.hswebframework.web"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket wechatDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("微信api"))
                .groupName("wechat")
                .ignoredParameterTypes(HttpSession.class, Authentication.class, HttpServletRequest.class, HttpServletResponse.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zmcsoft.rex.wechat"))
                .paths(PathSelectors.any())
                .build();
    }


    @Bean
    public Docket onlineStudy() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo("教育学习API"))
                .groupName("learn")
                .globalOperationParameters(tokenHeader())
                .ignoredParameterTypes(HttpSession.class, Authentication.class, HttpServletRequest.class, HttpServletResponse.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zmcsoft.rex.learn"))
                .paths(PathSelectors.any())
                .build();
    }


    public List<Parameter> tokenHeader() {
        ParameterBuilder tokenPar = new ParameterBuilder();

        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("x-access-token")
                .description("用户令牌: base64(des(openId或者userId))")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true).build();
        pars.add(tokenPar.build());
        return pars;
    }


    private ApiInfo apiInfo(String title) {
        return new ApiInfoBuilder()
                .title(title)
                .description("蓉e行交通众治平台")
                .termsOfServiceUrl("http://www.zmcsoft.com/")
                .version("1.0")
                .build();
    }

}
