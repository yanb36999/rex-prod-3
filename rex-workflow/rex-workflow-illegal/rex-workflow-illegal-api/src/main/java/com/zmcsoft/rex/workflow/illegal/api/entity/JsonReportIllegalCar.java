package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.naming.Name;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JsonReportIllegalCar implements Serializable {

    @JSONField(name = "c_id")
    private String idNo;

    @JSONField(name = "c_f_lsh")
    private String reportId;

    @JSONField(name = "c_hpzl")
    private String plateType;

    @JSONField(name = "c_hphm")
    private String plateNo;

    @JSONField(name = "c_cllx")
    private String carType;

    @JSONField(name = "c_jdczt")
    private String carStatus;

    @JSONField(name = "c_syr")
    private String owner;

    @JSONField(name = "c_syxz")
    private String userNature;

    @JSONField(name = "c_clsbdh")
    private String vin;

    @JSONField(name = "c_lxdh")
    private String contactPhone;

    @JSONField(name = "c_sjhm")
    private String phoneNo;

    @JSONField(name = "c_rlzl")
    private String fuelType;

    @JSONField(name = "c_clys")
    private String carColor;

    @JSONField(name = "c_clpp")
    private String carBrand;

    @JSONField(name = "c_qsbm")
    private String signDept;

    @JSONField(name = "c_qsbmmc")
    private String signDeptName;

    @JSONField(name = "c_qsfhr")
    private String signReviewer;

    @JSONField(name = "c_qsfhrxm")
    private String signReviewerName;

    @JSONField(name = "c_qsfhzt")
    private String signReviewStatus;


    @JSONField(name = "c_cflr")
    private String punishSignDept;

    @JSONField(name = "c_cflrmc")
    private String punishSignDeptName;

    @JSONField(name = "c_cflrzt")
    private String punishSignStatus;

    @JSONField(name = "c_cflrr")
    private String punishSignPerson;

    @JSONField(name = "c_cflrrxm")
    private String punishSignName;

    @JSONField(name = "c_cfjg")
    private String punishResult;

    @JSONField(name = "c_ajzzyy")
    private String stopCause;

    @JSONField(name = "c_wfbs")
    private Integer illegalCount;

    @JSONField(name = "c_yclbs")
    private String overCount;

    @JSONField(name = "c_tpdz")
    private List<String> imgPath;

    @JSONField(name = "c_tpxx")
    private List<String> imageBase64;

    @JSONField(name = "c_dxgzsj")
    private Date msgInformTime;

    @JSONField(name = "c_dxgzzt")
    private Byte msgInformStatus;

    @JSONField(name = "c_wxgzsj")
    private Date wechatInformTime;

    @JSONField(name = "c_wxgzzt")
    private String wechatInformStatus;

    @JSONField(name = "c_cjsj")
    private Date createTime;

    @JSONField(name = "c_gxsj")
    private Date updateTime;

    private Integer disposeStatus;


}
