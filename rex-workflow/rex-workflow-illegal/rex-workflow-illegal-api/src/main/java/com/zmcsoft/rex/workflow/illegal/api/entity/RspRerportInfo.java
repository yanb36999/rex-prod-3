package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

/**
 * 响应的举报信息
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RspRerportInfo extends JsonReportInfo{

    @JSONField(name = "f_id")
    private String id;
    //响应的车辆信息
    @JSONField(name = "wf_car")
    private RspReportIllegalCar wf_car;


    public RspRerportInfo of(ReportInfo reportInfo){
        setId(reportInfo.getId());
        setWaterId(reportInfo.getReportId());
        setName(reportInfo.getName());
        setReportDate(reportInfo.getReportDate());
        setOpenId(reportInfo.getOpenId());
        setIdNumber(reportInfo.getIdNumber());
        setPhone(reportInfo.getPhone());
        setDescribe(reportInfo.getDescribe());
        setAddress(reportInfo.getAddress());
        setIllegalDate(reportInfo.getReportDate());
        setReportType(reportInfo.getReportType());
        setDspStatus(reportInfo.getDspStatus());

        return this;
    }
}
