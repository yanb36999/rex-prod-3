package com.zmcsoft.rex.wechat;

import me.chanjar.weixin.common.util.crypto.SHA1;
import me.chanjar.weixin.mp.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 *
 * @author zhouhao
 * @since
 */
@Configuration
@ConditionalOnClass(WxMpService.class)
@EnableConfigurationProperties(WeChatProperties.class)
public class WebChatAutoConfiguration {

    public static void main(String[] args) {
        System.out.println(SHA1.gen("test"));
    }

    @Autowired
    private WeChatProperties properties;

    @Autowired(required = false)
    private List<AutoRegisterWxMpMessageHandler> messageHandlers;

    @Bean
    @ConditionalOnMissingBean
    public WxMpConfigStorage configStorage() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(this.properties.getAppId());
        configStorage.setSecret(this.properties.getSecret());
        configStorage.setToken(this.properties.getToken());
        configStorage.setAesKey(this.properties.getAesKey());
        return configStorage;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
// WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.okhttp.WxMpServiceImpl();
// WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.jodd.WxMpServiceImpl();
// WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.apache.WxMpServiceImpl();
        WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(configStorage);
        return wxMpService;
    }

    @Bean
    public WxMpMessageRouter router(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
        if (messageHandlers != null) {
            for (AutoRegisterWxMpMessageHandler messageHandler : messageHandlers) {
                for (String event : messageHandler.events()) {
                    WxMpMessageRouterRule routerRule = newRouter.rule();
                    routerRule.setAsync(messageHandler.async());
                    routerRule.setEvent(event);
                    routerRule.handler(messageHandler);
                    if (messageHandler.end()) {
                        routerRule.end();
                    } else {
                        routerRule.next();
                    }
                }
            }
        }
//// 记录所有事件的日志 （异步执行）
//        newRouter.rule().handler(this.logHandler).next();
//// 接收客服会话管理事件
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
//                .handler(this.kfSessionHandler).end();
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
//                .handler(this.kfSessionHandler)
//                .end();
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
//                .handler(this.kfSessionHandler).end();
//// 门店审核事件
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxMpEventConstants.POI_CHECK_NOTIFY)
//                .handler(this.storeCheckNotifyHandler).end();
//// 自定义菜单事件
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxConsts.BUTTON_CLICK).handler(this.getMenuHandler()).end();
//// 点击菜单连接事件
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxConsts.BUTTON_VIEW).handler(this.nullHandler).end();
//// 关注事件
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxConsts.EVT_SUBSCRIBE).handler(this.getSubscribeHandler())
//                .end();
//// 取消关注事件
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxConsts.EVT_UNSUBSCRIBE)
//                .handler(this.getUnsubscribeHandler()).end();
//// 上报地理位置事件
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxConsts.EVT_LOCATION).handler(this.getLocationHandler())
//                .end();
//// 接收地理位置消息
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_LOCATION)
//                .handler(this.getLocationHandler()).end();
//// 扫码事件
//        newRouter.rule().async(false).msgType(WxConsts.XML_MSG_EVENT)
//                .event(WxConsts.EVT_SCAN).handler(this.getScanHandler()).end();
//// 默认
//        newRouter.rule().async(false).handler(this.getMsgHandler()).end();
        return newRouter;
    }
}
