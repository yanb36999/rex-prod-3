package com.zmcsoft.rex.starter.message;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.zmcsoft.rex.message.AbstractAsyncMessageSender;
import com.zmcsoft.rex.message.AbstractMessageSenderProvider;
import com.zmcsoft.rex.message.MessageSenderProvider;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.message.sms.CdjgjSmsSender;
import com.zmcsoft.rex.message.sms.SMSSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class SMSConfiguration {

    @Bean
    @Profile("prod")
    public MessageSenderProvider<SMSSender> asyncSMSSender() {
        return new MessageSenderProvider<SMSSender>() {
            @Override
            public String getProvider() {
                return MessageSenders.DEFAULT_PROVIDER;
            }

            @Override
            public Class<SMSSender> getProviderClass() {
                return SMSSender.class;
            }

            @Override
            public SMSSender create() {
                return new CdjgjSmsSender();
            }
        };
    }

    @Bean
    @Profile({"dev", "test"})
    public AbstractMessageSenderProvider<SMSSender> mockTencentSMSSender() {
        Logger logger_ = LoggerFactory.getLogger("business.mockSmsSender");

        AbstractMessageSenderProvider<SMSSender> smsSender = new AbstractMessageSenderProvider<SMSSender>() {
            @Override
            public SMSSender create() {
                return new AsyncSMSSender(null) {
                    @Override
                    public boolean send() {
                        logger_.info("模拟发送短信[{}]到{}", content, phone);
                        return true;
                    }
                };
            }
        };

        smsSender.setProvider(MessageSenders.DEFAULT_PROVIDER);
        return smsSender;
    }

    public static class AsyncSMSSender extends AbstractAsyncMessageSender implements SMSSender {
        String phone;

        String content;

        SmsSingleSender sender;

        Logger logger;

        public AsyncSMSSender(SmsSingleSender sender) {
            this.sender = sender;
        }

        @Override
        public SMSSender to(String phone) {
            this.phone = phone;
            return this;
        }

        @Override
        public SMSSender content(String content) {
            this.content = content;

            return this;
        }

        @Override
        protected boolean doSend() {
            try {
                logger.debug("异步发送短信[{}]到{}", content, phone);
                SmsSingleSenderResult result =
                        sender.send(0, "86", phone, content, "", "123");
                logger.debug("异步发送短信结果:{}", result);
            } catch (Exception e) {
                logger.info("发送短信[{}]到{}失败", content, phone, e);
            }
            return false;
        }

        public void setLogger(Logger logger) {
            this.logger = logger;
        }
    }

    public static class TencentSMSSender extends AbstractMessageSenderProvider<SMSSender> {

        private String appKey;

        private int appId;

        SmsSingleSender sender;


        Logger logger = LoggerFactory.getLogger(TencentSMSSender.class);

        public void init() {
            try {
                SmsSingleSender sender = new SmsSingleSender(appId, appKey);
            } catch (Exception e) {
                //never be happen
            }

        }

        @Override
        public SMSSender create() {
            AsyncSMSSender asyncSMSSender = new AsyncSMSSender(sender);
            asyncSMSSender.setLogger(logger);
            return asyncSMSSender;
        }

        public void setAppId(int appId) {
            this.appId = appId;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public void setLogger(Logger logger) {
            this.logger = logger;
        }
    }
}
