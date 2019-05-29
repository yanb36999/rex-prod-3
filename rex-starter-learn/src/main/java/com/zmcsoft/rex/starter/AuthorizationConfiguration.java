package com.zmcsoft.rex.starter;

import com.zmcsoft.rex.api.user.authorization.CustomRexUserTokenParser;
import com.zmcsoft.rex.api.user.authorization.DESRexUserTokenParser;
import org.hswebframework.web.ThreadLocalUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author zhouhao
 * @since 1.0
 */
@Configuration
@EnableScheduling
public class AuthorizationConfiguration {

    @ConfigurationProperties(prefix = "rex.user-api")
    @Bean
    public CustomRexUserTokenParser customRexUserTokenParser() {
        return new DESRexUserTokenParser();
    }

    @Bean
    @SuppressWarnings("all")
    public ExecutorService executorService() {
        ThreadFactory threadFactory=   Executors.defaultThreadFactory();
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 25, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return threadFactory.newThread(()->{
                    try{
                        r.run();
                    }finally {
                        ThreadLocalUtils.clear();
                    }
                });
            }
        });
    }
}
