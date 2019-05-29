package com.zmcsoft.rex.tmb.local;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 * @since
 */
@Configuration
@SpringBootApplication
@WebAppConfiguration
@MapperScan("com.zmcsoft.rex")
public class MyBatisDaoTestApplication {
}
