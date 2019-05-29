package com.zmcsoft.rex.wechat;


import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WxSendTemplateMsgServiceImpl implements WxSendTemplateMsgService{

    @Autowired
    protected WxMpService wxService;

    /**
     *
     * @param openId  微信openId
     * @param dataMap 模板消息数据
     *                  违法决定书：{{keyword1}}
                        违法行为：{{keyword2}}
                        违法地点：{{keyword3}}
                        违法时间：{{keyword4}}
                        处理结果：{{keyword5}}
     * @param title    模板消息title
     * @param remark   模板消息备注
     * @return 模板消息ID
     */
    @Override
    public String sendChatTemplateMessage(String openId,Map<String,String> dataMap,String title,String remark) throws WxErrorException{
        List<WxMpTemplateData> dataList = new ArrayList<>();
        dataMap.forEach((key,value)->{
            WxMpTemplateData wxMpTemplateData = new WxMpTemplateData();
            wxMpTemplateData.setName(key);
            wxMpTemplateData.setValue(value);
            dataList.add(wxMpTemplateData);
        });
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .data(dataList)
                .templateId("Fz9Jv8JPrDlmWI6pVF5G20HiYDjo6NgZad8qY-hUig4").build();
        templateMessage.addWxMpTemplateData(
                new WxMpTemplateData("first", title));
        templateMessage.addWxMpTemplateData(
                new WxMpTemplateData("remark", remark));
        String msgId = this.wxService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        return msgId;
    }
}
