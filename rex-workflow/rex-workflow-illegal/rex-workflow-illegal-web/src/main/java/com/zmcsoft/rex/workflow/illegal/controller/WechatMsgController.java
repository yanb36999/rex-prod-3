package com.zmcsoft.rex.workflow.illegal.controller;

import com.alibaba.fastjson.JSON;
import com.zmcsoft.rex.message.MessageSenders;
import com.zmcsoft.rex.workflow.illegal.api.entity.CarIllegalCaseHandle;
import com.zmcsoft.rex.workflow.illegal.api.entity.WechatSendLogger;
import com.zmcsoft.rex.workflow.illegal.api.service.CarIllegalCaseHandleService;
import com.zmcsoft.rex.workflow.illegal.api.service.IllegalCodeService;
import com.zmcsoft.rex.workflow.illegal.api.service.WechatSendLoggerService;
import com.zmcsoft.rex.workflow.illegal.impl.message.IllegalMessageSendTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/wechat-msg")
@Api(tags = "微信推送")
@Slf4j(topic = "business.wechat-msg")
public class WechatMsgController {

    @Autowired
    private MessageSenders messageSenders;

    @Autowired
    private WechatSendLoggerService wechatSendLoggerService;


    @Autowired
    private IllegalCodeService illegalCodeService;

    @Autowired
    private CarIllegalCaseHandleService carIllegalCaseHandleService;

    @PostMapping("/again")
    @ApiOperation("重新推送微信消息")
    public ResponseMessage<List<WechatSendLogger>> againSend(@RequestBody String decisionNo){

        long startTime = System.currentTimeMillis();
        String decisionNoStr = JSON.parseObject(decisionNo).get("decisionNo").toString().replace("\n", "");
        String[] split = decisionNoStr.split(",");

        for (int i = 0; i < split.length; i++) {
            CarIllegalCaseHandle byDecisionNo = carIllegalCaseHandleService.getByDecisionNo(split[i].replace("\"",""));
            if (byDecisionNo!=null){
//                String userId = byDecisionNo.getUserId();
//                StringBuilder sb = new StringBuilder();
//                sb.append("您的违法行为已处理（决定书编号：")
//                        .append(byDecisionNo.getDecisionNumber())
//                        .append("），请及时缴纳罚款，逾期将产生滞纳金。（如您已成功缴款，请不要重复缴款）");
//                log.info("微信推送信息正式:用户{},内容{}",userId,sb);
////                发送微信消息提醒
//                messageSenders.wechat()
//                        .to(userId)
//                        .content(sb+"") // TODO: 2017/11/7 提示内容待修改
//                        .keyword("违法处理成功")
//                        .title("成都交警温馨提示")
//                        .send();


                Date illegalTime = byDecisionNo.getIllegalCase().getIllegalTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String userId = byDecisionNo.getUserId();
                String decisionNumber = byDecisionNo.getDecisionNumber();
                String illegalActivities = byDecisionNo.getIllegalCase().getIllegalActivities();
                String illegalContent = illegalCodeService.getByCode(illegalActivities).getIllegalContent();
                String illegalAddress = byDecisionNo.getIllegalCase().getIllegalAddress();
                String time = simpleDateFormat.format(illegalTime);
                String punishResult = byDecisionNo.getIllegalCase().getPayMoney()+"元/"+byDecisionNo.getIllegalCase().getScore()+"分";

                String wtetle = byDecisionNo.getIllegalCase().getPlateNumber()+"的交通违法处理已申请成功";
                messageSenders.wechat("illegal")
                        .template(IllegalMessageSendTemplate.builder()
                                .illegalDecision(decisionNumber)
                                .illegalBehavior(illegalContent)
                                .illegalAddress(illegalAddress)
                                .illeaglTime(time)
                                .punishResult(punishResult).build().getParamMap())
                        .title(wtetle)
                        .to(userId)
                        .send();
            }
        }
        long endTime = System.currentTimeMillis();
        List<WechatSendLogger> wechatSendLoggers = wechatSendLoggerService.queryByTime(null,startTime, endTime);

        return ResponseMessage.ok(wechatSendLoggers);
    }
}
