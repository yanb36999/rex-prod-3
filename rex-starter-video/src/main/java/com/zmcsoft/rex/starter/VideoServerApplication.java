package com.zmcsoft.rex.starter;

import org.hswebframework.web.loggin.aop.EnableAccessLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAspectJAutoProxy
@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
@EnableCaching //开启缓存
@EnableAccessLogger //开启访问日志
@ComponentScan("com.zmcsoft")
@EnableScheduling
public class VideoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoServerApplication.class,args);
    }
}
