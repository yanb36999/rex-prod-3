package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JsonReportPunish {

    @JSONField(name = "id")
    private String id;

    @JSONField(name = "car_id")
    private String carId;

    @JSONField(name = "jdslb")
    private String decisionType;

    @JSONField(name = "jdsbh")
    private String decisionNo;

    @JSONField(name = "wsjyw")
    private String checkNo;

    @JSONField(name = "cljg")
    private String dspOffice;

    @JSONField(name = "cljgmc")
    private String dspOfficeName;

    @JSONField(name = "clsj")
    private String dspTime;

    @JSONField(name = "jkbj")
    private String paySign;

    @JSONField(name = "jkrq")
    private String payTime;

    @JSONField(name = "lrr")
    private String enterPerson;

    @JSONField(name = "lrsj")
    private String enterDate;



}
