package com.zmcsoft.rex.wechat;

import me.chanjar.weixin.mp.api.WxMpMessageHandler;

/**
 * @author zhouhao
 * @since
 */
public interface AutoRegisterWxMpMessageHandler extends WxMpMessageHandler {
    boolean async();

    String msgType();

    String[] events();

    boolean end();
}
