package com.zmcsoft.rex.wechat;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChatMsgTestApplication.class)
public class SendMsgTest {

    @Autowired
    protected WxMpService wxService;

    @Autowired
    protected WxSendTemplateMsgService wxSendTemplateMsgService;

    @Test
    public void sendMsgTest() throws WxErrorException {
        List<WxMpTemplateData> dataList = new ArrayList<>();
        Map<String, String> dataMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dataMap.put("keyword1", "123456789");
        dataMap.put("keyword2", "闯红灯");
        dataMap.put("keyword3", "四川某某大道");
        dataMap.put("keyword4", dateFormat.format(new Date()));
        dataMap.put("keyword5", "200元/3分");
        String result = wxSendTemplateMsgService.sendChatTemplateMessage("o9IjmjuJZmRWMiOUZx_qBLgDEc88", dataMap, "申请成功", "请及时缴纳罚款，逾期将产生滞纳金");
        Assert.assertNotNull(result);
    }

    @Test
    public void sendUploadFile() throws Exception {
        WxMediaUploadResult result = wxService.getMaterialService()
                .mediaUpload(WxConsts.MEDIA_IMAGE,
                        new File("/home/zhouhao/桌面/logo.jpg"));

        System.out.println(wxService.getMaterialService().mediaDownload(result.getMediaId()));
        System.out.println(result.getMediaId());
    }
}