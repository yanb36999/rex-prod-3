package com.zmcsoft.rex.wechat;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 * @since
 */
@Component
public class TestMessageHandler implements AutoRegisterWxMpMessageHandler {
    @Override
    public boolean async() {
        return false;
    }

    @Override
    public String msgType() {
        return WxConsts.CUSTOM_MSG_TEXT;
    }

    @Override
    public String[] events() {
        return new String[]{null};
    }

    @Override
    public boolean end() {
        return true;
    }

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        return WxMpXmlOutMessage.TEXT()
                .toUser(wxMessage.getFromUser())
                .fromUser(wxMessage.getToUser())
                .content("欢迎加入hsweb")
                .build();
    }
}
