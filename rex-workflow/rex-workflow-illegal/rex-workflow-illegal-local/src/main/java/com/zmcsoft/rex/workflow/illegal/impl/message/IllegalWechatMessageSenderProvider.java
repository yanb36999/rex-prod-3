package com.zmcsoft.rex.workflow.illegal.impl.message;

import com.alibaba.fastjson.JSONObject;
import com.zmcsoft.rex.message.AbstractMessageSenderProvider;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.message.wechat.WechatMessageSender;
import com.zmcsoft.rex.workflow.illegal.api.entity.WechatSendLogger;
import com.zmcsoft.rex.workflow.illegal.api.service.WechatSendLoggerService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;


/**
 * @author zhouhao
 * @since 1.0
 */
@Slf4j(topic = "business.wechat-message")
@Service
public class IllegalWechatMessageSenderProvider extends AbstractMessageSenderProvider<WechatMessageSender> {
    private String justSendTo = "o9IjmjruM8MJV8MTm95qWtdHSSSc";

    @Autowired
    private WechatSendLoggerService wechatSendLoggerService;

    private WxMpService wxMpService;

    @Autowired(required = false)
    public void setWxMpService(WxMpService wxMpService) {
        this.wxMpService = wxMpService;
    }

    @Override
    public String getProvider() {
        return "illegal";
    }

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        if (wxMpService == null) {
            WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
            configStorage.setAppId("wxb492374e5ba6759f");
            configStorage.setSecret("539151c8ebcdd7a72bb5a115878c9f67");
            configStorage.setToken("9661a5ea26eb4cd3af234445fff1e866");
            configStorage.setAesKey("94EBtz1kgvwra9DcAKIRepSvlwLikzq3iF97VviHANx");
            WxMpServiceImpl wxMpService = new WxMpServiceImpl();
            wxMpService.setWxMpConfigStorage(configStorage);
            this.wxMpService = wxMpService;
        }
    }

    @Override
    public WechatMessageSender create() {
        return new AsyncWechatMessageSender() {
            @Override
            protected boolean doSend() {
                List<WxMpTemplateData> dataList = new ArrayList<>();
                String openId = getOpenId();
                if (!Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
                    openId = justSendTo;
                }
                Map<String, String> template = getParameters();
                log.info("开始推送违法微信信息:{}->{}", openId, template);
                template.forEach((key, value) -> {
                    WxMpTemplateData wxMpTemplateData = new WxMpTemplateData();
                    wxMpTemplateData.setName(key);
                    wxMpTemplateData.setValue(value);
                    dataList.add(wxMpTemplateData);
                });
                WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                        .toUser(openId)
                        .data(dataList)
                        .templateId("0CJgZiN39j8cssKjVNPKzNuU5qG8ipeIIyfC4aMXRj8").build();
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("first", template.get("title")));
                templateMessage.addWxMpTemplateData(
                        new WxMpTemplateData("remark", "请及时缴纳罚款，逾期将产生滞纳金"));
                String msgId;
                String comment;
                try {
                    msgId = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                    comment = msgId;
                } catch (WxErrorException we) {
                    log.error("推送违法微信信息错误:{}", we.getMessage());
                    msgId = "";
                    comment = we.getMessage();
                }
                String sendStatus = StringUtils.hasText(msgId) ? "1" : "0";
                WechatSendLogger resultWechatSendLogger = WechatSendLogger.builder()
                        .openId(openId)
                        .content(JSONObject.toJSONString(template))
                        .createTime(new Date())
                        .title(template.get("title"))
                        .comment(comment)
                        .sendStatus(sendStatus)
                        .build();
                wechatSendLoggerService.insert(resultWechatSendLogger);
                return true;
            }
        };
    }
}
