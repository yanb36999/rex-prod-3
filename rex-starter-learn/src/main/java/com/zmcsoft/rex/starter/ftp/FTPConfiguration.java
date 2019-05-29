package com.zmcsoft.rex.starter.ftp;

import com.zmcsoft.rex.message.AbstractMessageSenderProvider;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.message.ftp.FTPMessage;
import com.zmcsoft.rex.message.ftp.FTPMessageSender;
import com.zmcsoft.rex.message.ftp.FTPMessageSenderProvider;
import com.zmcsoft.rex.message.ftp.pool.FTPClientConfigure;
import com.zmcsoft.rex.message.ftp.pool.FTPClientFactory;
import com.zmcsoft.rex.message.ftp.pool.FTPClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FTPConfiguration {

    static FTPClientPool createFtpPool(FTPClientConfigure clientConfigure) {
//        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
//        config.setJmxEnabled(false);
//        config.setJmxNameBase(FTPClientPool.class.getSimpleName() + "_");
//        config.setJmxNamePrefix(FTPClientPool.class.getSimpleName());
//        FTPClientPool pool = new FTPClientPool(new FTPClientFactory(clientConfigure), clientConfigure);

        return new FTPClientPool(new FTPClientFactory(clientConfigure), clientConfigure);
    }

    static AbstractMessageSenderProvider<FTPMessageSender> createMockFtpSender(String provider) {
        return new AbstractMessageSenderProvider<FTPMessageSender>() {
            Logger logger = LoggerFactory.getLogger("MockFTPMessageSenderProvider." + provider);

            @Override
            public String getProvider() {
                return provider;
            }

            @Override
            public FTPMessageSender create() {
                return new FTPMessageSender() {
                    @Override
                    public FTPMessageSender message(FTPMessage message) {
                        logger.debug("调用ftp:{}", message);
                        return this;
                    }

                    @Override
                    public boolean send() {
                        return true;
                    }
                };
            }
        };
    }

    @Bean
    @ConfigurationProperties(prefix = "com.zmcsoft.learn.ftp")
    public FTPClientConfigure defaultClientConfigure() {
        return new FTPClientConfigure();
    }

    //生产环境的默认配置
    @Profile({"prod"})
    @Configuration
    public static class ProductionFTPClientConfiguration {

        @Bean
        //默认的ftp消息发送
        public FTPMessageSenderProvider defaultFTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
            FTPMessageSenderProvider ftpMessageSenderProvider = new FTPMessageSenderProvider(createFtpPool(defaultClientConfigure));
            ftpMessageSenderProvider.setLogger(LoggerFactory.getLogger("business.ftpSender"));
            ftpMessageSenderProvider.setProvider(MessageSenders.DEFAULT_PROVIDER);
            return ftpMessageSenderProvider;
        }

        @Bean
        //特定的ftp消息发送
        public FTPMessageSenderProvider dataIn3FTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
            FTPMessageSenderProvider ftpMessageSenderProvider = new FTPMessageSenderProvider(createFtpPool(defaultClientConfigure));
            ftpMessageSenderProvider.setLogger(LoggerFactory.getLogger("business.ftpSender"));
            //设置特定的provider
            //通过messageSenders.ftp("data-in-3")..进行操作
            ftpMessageSenderProvider.setProvider("learn");
            return ftpMessageSenderProvider;
        }

        @Bean
        public FTPMessageSenderProvider intoCityFTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
            FTPMessageSenderProvider ftpMessageSenderProvider = new FTPMessageSenderProvider(createFtpPool(defaultClientConfigure));
            ftpMessageSenderProvider.setLogger(LoggerFactory.getLogger("business.ftpSender"));
            ftpMessageSenderProvider.setProvider("intoCityCard");
            return ftpMessageSenderProvider;
        }
    }


    //测试环境配置
    @Profile("test")
    @Configuration
    public static class CustomFTPClientConfiguration {

        //测试环境下为了不影响生产环境默认ftp使用模拟的方式。
        @Bean
        public AbstractMessageSenderProvider<FTPMessageSender> defaultFTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
            return createMockFtpSender(MessageSenders.DEFAULT_PROVIDER);
        }

        @Bean
        public FTPMessageSenderProvider intoCityFTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
            FTPMessageSenderProvider ftpMessageSenderProvider = new FTPMessageSenderProvider(createFtpPool(defaultClientConfigure));
            ftpMessageSenderProvider.setLogger(LoggerFactory.getLogger("business.ftpSender"));
            ftpMessageSenderProvider.setProvider("intoCityCard");
            return ftpMessageSenderProvider;
        }

       // @Bean
     //   public AbstractMessageSenderProvider<FTPMessageSender> intoCityFTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
//            FTPMessageSenderProvider ftpMessageSenderProvider = new FTPMessageSenderProvider(createFtpPool(defaultClientConfigure));
//            ftpMessageSenderProvider.setLogger(LoggerFactory.getLogger("business.ftpSender"));
//            ftpMessageSenderProvider.setProvider("intoCityCard");
      //      return createMockFtpSender("intoCityCard");
       // }
//        @Bean
//        //默认的ftp消息发送
//        public FTPMessageSenderProvider defaultFTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
//            FTPMessageSenderProvider ftpMessageSenderProvider = new FTPMessageSenderProvider(createFtpPool(defaultClientConfigure));
//            ftpMessageSenderProvider.setLogger(LoggerFactory.getLogger("business.ftpSender"));
//            ftpMessageSenderProvider.setProvider(MessageSenders.DEFAULT_PROVIDER);
//            return ftpMessageSenderProvider;
//        }

//        @Bean
//        public FTPMessageSenderProvider learnFTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
//            FTPMessageSenderProvider ftpMessageSenderProvider = new FTPMessageSenderProvider(createFtpPool(defaultClientConfigure));
//            ftpMessageSenderProvider.setLogger(LoggerFactory.getLogger("business.ftpSender"));
//            //设置特定的provider
//            //通过messageSenders.ftp("data-in-3")..进行操作
//            // TODO: 2017/11/21 命名应根据实际功能命名，比如: illegal.report
//            ftpMessageSenderProvider.setProvider("learn");
//            return ftpMessageSenderProvider;
//        }

            @Bean
            public AbstractMessageSenderProvider<FTPMessageSender> learnFTPMessageSenderProvider(FTPClientConfigure defaultClientConfigure) {
                return createMockFtpSender("learn");
            }


    }

    //开发环境配置,全部使用模拟方式
    @Profile("dev")
    @Configuration
    public static class DevFTPClientConfiguration {

        @Bean
        public AbstractMessageSenderProvider<FTPMessageSender> defaultFTPMessageSenderProvider() {
            return createMockFtpSender(MessageSenders.DEFAULT_PROVIDER);
        }

        @Bean
        public AbstractMessageSenderProvider<FTPMessageSender> learnFTPMessageSenderProvider() {
            return createMockFtpSender("learn");
        }
    }


}
