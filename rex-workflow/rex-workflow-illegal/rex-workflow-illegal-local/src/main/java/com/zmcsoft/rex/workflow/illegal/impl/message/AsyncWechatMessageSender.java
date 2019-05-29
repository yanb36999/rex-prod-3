package com.zmcsoft.rex.workflow.illegal.impl.message;

import com.zmcsoft.rex.message.AbstractAsyncMessageSender;
import com.zmcsoft.rex.message.wechat.WechatMessageSender;
import com.zmcsoft.rex.workflow.illegal.api.entity.WechatSendLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouhao
 * @since 1.0
 */
public abstract class AsyncWechatMessageSender extends AbstractAsyncMessageSender implements WechatMessageSender {
    protected Map<String, String> parameters       = new HashMap<>();
    protected WechatSendLogger    wechatSendLogger = WechatSendLogger.builder().build();
    protected String openId;

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getOpenId() {
        return openId;
    }

    @Override
    public WechatMessageSender to(String openId) {
        this.openId=openId;
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


}