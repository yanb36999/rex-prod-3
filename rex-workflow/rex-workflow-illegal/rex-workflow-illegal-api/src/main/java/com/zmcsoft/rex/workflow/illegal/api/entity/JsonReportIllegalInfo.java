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
public class JsonReportIllegalInfo implements Serializable{

    @JSONField(name = "i_id")
    private String id;

    @JSONField(name = "i_car_id")
    private String carId;
    //违法录入机关
    @JSONField(name = "i_cjjg")
    private String enterOfficeCode;

    @JSONField(name = "i_cjjgmc")
    private String enterOfficeName;

    @JSONField(name = "i_hpzl")
    private String plateType;

    @JSONField(name = "i_hphm")
    private String plateNo;

    @JSONField(name = "i_syxz")
    private String useNature;

    @JSONField(name = "i_fdjh")
    private String engineCode;

    @JSONField(name = "i_clsbdh")
    private String vin;

    @JSONField(name = "i_csys")
    private String carColor;

    @JSONField(name = "i_clpp")
    private String carBrand;

    @JSONField(name = "i_jtfs")
    private String trafficeWay;

    @JSONField(name = "i_cjfs")
    private String collectWay;

    @JSONField(name = "i_wfsj")
    private Date illegaltime;

    @JSONField(name = "i_xzqh")
    private String adminDivision;

    @JSONField(name = "i_wfdd")
    private String illegalAddressCode;

    @JSONField(name = "i_wfdz")
    private String illegalSite;

    @JSONField(name = "i_lddm")
    private String roadCode;

    @JSONField(name = "i_lkldmc")
    private String roadName;

    @JSONField(name = "i_wfxwmc")
    private String illegalBehaviorName;

    @JSONField(name = "i_wfxw")
    private String illegalBehaviorCode;


    @JSONField(name = "i_fkje")
    private Integer illegalFine;

    @JSONField(name = "i_wfjf")
    private Integer illegalScore;

    //违法行为处理状态
    /**
     * @see HandleStatusDefine
     */
    @JSONField(name = "i_wfclzt")
    private String dspStatus;


    //违法信息来源（1、网上申请；2、现场处罚）
    @JSONField(name = "i_wfly")
    private String illegalInfoSource;

    @JSONField(name = "i_f_lsh")
    private String reportId;

    @JSONField(name = "i_qxcfbs") //取消处罚标识
    private String cancelFlag;

    @JSONField(name = "i_qxcfyy") //取消处罚原因
    private String cancelReason;


//    @JSONField(name = "ryfl")
//    private Byte personType;
//
//    @JSONField(name = "jszh")
//    private String driverNo;
//
//    @JSONField(name = "dabh")
//    private String fileNo;
//
//    @JSONField(name = "fzjg")
//    private String sendLicenceOffice;
//
//    @JSONField(name = "zjcx")
//    private String driverCarType;
//
//    @JSONField(name = "dsr")
//    private String parties;
//
//    @JSONField(name = "zsxzqh")
//    private String homeAdminDivision;
//
//    @JSONField(name = "zsxxdz")
//    private String detailSite;
//
//    @JSONField(name = "lxfs")
//    private String contactWay;
//
//    @JSONField(name = "clfl")
//    private Byte carType;
//
//
//    @JSONField(name = "jdcsyr")
//    private String carOwner;
//
//
//
//    @JSONField(name = "zqmj")
//    private String dutyPolice;
//
//    @JSONField(name = "jkfs")
//    private String payWay;

    @JSONField(name = "cljg")
    private String dspOffice;

    @JSONField(name = "cljgmc")
    private String dspOfficeName;

    @JSONField(name = "clsj")
    private Date dspDate;

    @JSONField(name = "jkbj")
    private String paySign;

    @JSONField(name = "jkrq")
    private Date payDate;

    @JSONField(name = "lrr")
    private String dataEntryClerk;

    @JSONField(name = "lrsj")
    private Date dataEntryTime;
//
//    @JSONField(name = "jbr1")
//    private String responsibleOne;
//
//    @JSONField(name = "jbr2")
//    private String responsibleTwo;
//
//    @JSONField(name = "xxly")
//    private Byte infoSource;
//
//    @JSONField(name = "xrms")
//    private Byte writePattern;
//
//    @JSONField(name = "gxsj")
//    private Date updateTime;
//
//    @JSONField(name = "zjmc")
//    private String licenseName;
//
//    @JSONField(name = "cclzrq")
//    private String firstGetDate;


    @JSONField(name = "jdslb")
    private String decisionType;

    @JSONField(name = "jdsbh")
    private String decisionNo;

    @JSONField(name = "wsjyw")
    private String checkNo;


    @JSONField(name = "qxcfyy")
    private String cancelCause;

    @JSONField(name = "qxcfbs")
    private String cancelSign;

}
