package com.zmcsoft.rex.pay.datasource;

import org.hswebframework.web.datasource.DynamicDataSourceAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author zhouhao
 * @since
 */
@Configuration
public class MultipleDataSourceAutoConfiguration {

    @ConfigurationProperties("spring.datasource")
    @Primary
    @Bean
    public DataSourceProperties defaultProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource")
    @Primary
    public DataSource dataSource() {
        return defaultProperties().initializeDataSourceBuilder().build();
    }

//    @Bean
//    public DynamicDataSourceAutoConfiguration.AutoRegisterDataSource autoRegisterDataSource() {
//        return new DynamicDataSourceAutoConfiguration.AutoRegisterDataSource();
//    }

    @ConfigurationProperties("logger-db.datasource")
    @Bean
    public DataSourceProperties logDbProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("logger-db.datasource")
    public DataSource loggerDb() {
        return logDbProperties().initializeDataSourceBuilder().build();
    }

}
