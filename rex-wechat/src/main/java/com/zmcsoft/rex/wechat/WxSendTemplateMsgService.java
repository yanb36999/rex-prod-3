package com.zmcsoft.rex.wechat;

import me.chanjar.weixin.common.exception.WxErrorException;

import java.util.Map;

public interface WxSendTemplateMsgService {

    String sendChatTemplateMessage(String openId, Map<String,String> dataMap, String title, String remark) throws WxErrorException;
}
