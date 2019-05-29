package com.zmcsoft.rex.workflow.illegal.impl.message;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IllegalMessageSendTemplate {

    //违法决定书
    private String illegalDecision;

    //违法行为
    private String illegalBehavior;

    //违法地址
    private String illegalAddress;

    //违法时间
    private String illeaglTime;

    //处罚结果
    private String punishResult;

    public Map<String,String> getParamMap(){
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("keyword1",this.illegalDecision);
        dataMap.put("keyword2",this.illegalBehavior);
        dataMap.put("keyword3",this.illegalAddress);
        dataMap.put("keyword4",this.illeaglTime);
        dataMap.put("keyword5",this.punishResult);
        return dataMap;
    }
}
