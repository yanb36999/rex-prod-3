package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JsonReportInfo implements Serializable {

    @JSONField(name = "f_lsh")
    private String waterId;

    @JSONField(name = "f_openid")
    private String openId;

    @JSONField(name = "f_xm")
    private String name;

    @JSONField(name = "f_jbsj")
    private Date reportDate;

    @JSONField(name = "f_sfzhm")
    private String idNumber;

    @JSONField(name = "f_lxrdh")
    private String phone;

    @JSONField(name = "f_smms")
    private String describe;

    @JSONField(name = "f_wfdz")
    private String address;

    @JSONField(name = "f_wflx")
    private String reportType;

    @JSONField(name = "f_wfjd")
    private String longitude;

    @JSONField(name = "f_wfwd")
    private String latitude;

    @JSONField(name = "f_spdz")
    private String videoUrl;

    @JSONField(name = "f_ypdz")
    private String radioUrl;

    @JSONField(name="f_wfsj")
    private Date illegalDate;

    @JSONField(name = "f_clzt")
    private String dspStatus;
}
