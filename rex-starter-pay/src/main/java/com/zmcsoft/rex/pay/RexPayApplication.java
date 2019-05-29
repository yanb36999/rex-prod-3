package com.zmcsoft.rex.pay;
import org.hswebframework.web.authorization.basic.configuration.EnableAopAuthorize;
import org.hswebframework.web.dao.Dao;
import org.hswebframework.web.loggin.aop.EnableAccessLogger;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动全部服务
 *
 * @author zhouhao
 * @since 1.0
 */
@EnableAspectJAutoProxy
@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
@EnableCaching //开启缓存
@EnableAccessLogger //开启访问日志
@ComponentScan("com.zmcsoft")
@MapperScan(value = "com.zmcsoft", markerInterface = Dao.class) //扫描mybatis dao
@EnableAopAuthorize
@EnableScheduling
@ServletComponentScan("com.zmcsoft.rex.pay.druid")
public class RexPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RexPayApplication.class,args);
    }
}
