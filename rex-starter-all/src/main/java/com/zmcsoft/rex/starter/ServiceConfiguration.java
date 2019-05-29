package com.zmcsoft.rex.starter;

import com.zmcsoft.rex.tmb.illegal.service.MemoryNunciatorService;
import com.zmcsoft.rex.tmb.illegal.service.NunciatorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {
    @Bean
    @ConditionalOnMissingBean(NunciatorService.class)
    public MemoryNunciatorService memoryNunciatorService() {
        return new MemoryNunciatorService();
    }

//    @Bean
//    public SchedulingConfigurer schedulingConfigurer() {
//        return taskRegistrar -> {
//            ThreadFactory threadFactory = Executors.defaultThreadFactory();
//            taskRegistrar.setScheduler(Executors.newSingleThreadScheduledExecutor(r -> {
//                Runnable proxy = () -> {
//                    try {
//                        r.run();
//                    } finally {
//                        ThreadLocalUtils.clear();
//                    }
//                };
//                return threadFactory.newThread(proxy);
//            }));
//        };
//    }
}
