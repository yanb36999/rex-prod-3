package com.zmcsoft.rex.workflow.illegal.impl.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmcsoft.rex.message.AbstractAsyncMessageSender;
import com.zmcsoft.rex.message.AbstractMessageSenderProvider;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.message.wechat.WechatMessageSender;
import com.zmcsoft.rex.workflow.illegal.api.entity.WechatSendLogger;
import com.zmcsoft.rex.workflow.illegal.api.service.WechatSendLoggerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.expands.request.http.HttpRequest;
import org.hswebframework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "business.wechat-message")
@Service
public class CWTWechatMessageSenderProvider extends AbstractMessageSenderProvider<WechatMessageSender> {

    private String apiUrl = "http://178.25.1.36/cdjj/intercdjj/icbcmsg";

    private String key = "70*j82BF#VX4dlfPS!C8";

    @Autowired
    private Environment environment;

    @Override
    public String getProvider() {
        return MessageSenders.DEFAULT_PROVIDER;
    }

    /**
     * 只推送给此人,如果要推送给真实的用户,设置为null即可
     */
    private String justSendTo = "o9IjmjruM8MJV8MTm95qWtdHSSSc";

    @Autowired
    private WechatSendLoggerService wechatSendLoggerService;

    private RequestBuilder builder = new SimpleRequestBuilder();

    public HttpRequest createRequest() {
        return builder.http(apiUrl);
    }

    @Override
    public WechatMessageSender create() {
        return new AsyncWechatMessageSender();
    }

    public class AsyncWechatMessageSender extends AbstractAsyncMessageSender implements WechatMessageSender {
        Map<String, String> parameters = new HashMap<>();
        WechatSendLogger wechatSendLogger = WechatSendLogger.builder().build();

        @Override
        public WechatMessageSender to(String openId) {
            if (!Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
                openId = justSendTo;
            }
            parameters.put("open_id", openId);
            wechatSendLogger.setOpenId(openId);
            return this;
        }

        @Override
        public WechatMessageSender content(String content) {
            wechatSendLogger.setContent(content);
            parameters.put("content", content);
            return this;
        }

        @Override
        public WechatMessageSender keyword(String keyword) {
            wechatSendLogger.setKeyword(keyword);
            parameters.put("keyword", keyword);
            return this;
        }

        @Override
        public WechatMessageSender title(String title) {
            wechatSendLogger.setTitle(title);
            parameters.put("title", title);
            return this;
        }

        @Override
        public WechatMessageSender template(Map<String, String> templateVar) {
            parameters.putAll(templateVar);
            return this;
        }

        @Override
        public WechatMessageSender template(String key, String value) {
            parameters.put(key, value);
            return this;
        }

        @Override
        protected boolean doSend() {
            if (null == wechatSendLogger.getOpenId()) {
                log.warn("未知指定微信推送用户,推送给默认用户:{}", justSendTo);
                to(justSendTo);
            }
            String data = JSONObject.toJSONString(parameters);
            try {
                Long timestamp = System.currentTimeMillis();
                String md5Resutl = DigestUtils.md5Hex(key + timestamp);
                log.debug("开始推送微信消息:timestamp={}&sign={}&data={} ", timestamp,md5Resutl , data);
                String result = createRequest().param("data", data)
                        .param("timestamp", String.valueOf(timestamp))
                        .param("sign", md5Resutl)
                        .post().asString();
                log.debug("消息推送服务返回:{}", result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                String code = jsonObject.getString("code");
                boolean success = "1000".equals(code);
                wechatSendLogger.setResult(jsonObject.getString("msg"));

                if (success) {
                    wechatSendLogger.setSendStatus("1");
                    log.debug("推送微信消息成功:{} ", parameters);
                } else {
                    log.error("推送微信消息失败:\n{} \n{}", parameters, result);
                    wechatSendLogger.setSendStatus("0");
                }

            } catch (IOException e) {
                wechatSendLogger.setSendStatus("0");
                log.error("推送微信消息失败:{}", parameters, e);
                wechatSendLogger.setResult(StringUtils.throwable2String(e));
                //  throw new RuntimeException("消息推送失败", e);
            }
            try {
                wechatSendLoggerService.insert(wechatSendLogger);
            } catch (Exception e) {
                log.error("保存微信推送日志失败:{}", JSON.toJSONString(wechatSendLogger), e);
            }
            return true;
        }
    }
}
