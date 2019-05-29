package com.zmcsoft.rex.tmb;

import com.zmcsoft.rex.tmb.illegal.service.MemoryNunciatorService;
import com.zmcsoft.rex.tmb.illegal.service.NunciatorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {
    @Bean
    @ConditionalOnMissingBean(NunciatorService.class)
    public MemoryNunciatorService memoryNunciatorService(){
        return new MemoryNunciatorService();
    }
}
