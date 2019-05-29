package com.zmcsoft.rex.workflow.illegal.api.entity;

//响应Json 形式的car数据

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RspReportIllegalCar extends JsonReportIllegalCar{
    //网上调查申请信息
    private ReportApplyInfo wf_apply;

    //违法信息
    private List<JsonReportIllegalInfo> wf_info;

//    //违章处理数据
//    private List<JsonReportPunish> wf_punish;

}
