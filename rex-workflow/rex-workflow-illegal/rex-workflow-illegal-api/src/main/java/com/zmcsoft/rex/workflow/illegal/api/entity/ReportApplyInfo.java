package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;
import org.hswebframework.web.id.IDGenerator;

import java.util.Date;


@Getter
@Setter
@Builder
@ApiModel(description = "网上调查处理申请信息")
@AllArgsConstructor
@NoArgsConstructor
public class ReportApplyInfo {

    @JSONField(name = "w_id")
    private String id;

    @ApiModelProperty(value = "微信用户Id")
    @JSONField(name = "w_openid")
    private String openId;

    @ApiModelProperty(value = "驾照信息")
    @JSONField(name = "w_zjzl")
    private String licenseType;

    @ApiModelProperty(value = "证件号码")
    @JSONField(name = "w_zjhm")
    private String licenseNo;

    @ApiModelProperty(value = "当事人")
    @JSONField(name = "w_dsr")
    private String parties;

    @ApiModelProperty(value = "档案编号")
    @JSONField(name = "w_ddbh")
    private String fileNo;

    @ApiModelProperty(value = "驾驶证记分")
    @JSONField(name = "w_jszjf")
    private Integer licenseScore;

    @ApiModelProperty(value = "驾驶证状态")
    @JSONField(name = "w_jszzt")
    private String licenseStatus;

    @ApiModelProperty(value = "联系电话")
    @JSONField(name = "w_lxdh")
    private String contactPhone;

    @ApiModelProperty(value = "发证机关")
    @JSONField(name = "w_fzjg")
    private String giveOffice;

    @ApiModelProperty(value = "准驾车型")
    @JSONField(name = "w_zjcx")
    private String driverType;

    @ApiModelProperty(value = "住所详细地址")
    @JSONField(name = "w_zsxxdz")
    private String homeAddress;


    @ApiModelProperty(value = "提交申请时间")
    @JSONField(name = "w_tjsqsj")
    private Date submitTime;

    @ApiModelProperty(value = "提交申请违法笔数")
    @JSONField(name = "w_tjsqwfbs")
    private String submitCount;


    @ApiModelProperty(value = "号牌种类")
    @JSONField(name = "w_hpzl")
    private String plateType;

    @ApiModelProperty(value = "号牌号码")
    @JSONField(name = "w_hphm")
    private String plateNo;

    @ApiModelProperty(value = "操作时间")
    @JSONField(name = "w_cxsj")
    private Date operationTime;

    @ApiModelProperty(value = "操作内容")
    @JSONField(name = "w_czlr")
    private String operationContent;

    @ApiModelProperty(value = "操作结果")
    @JSONField(name = "w_czjg")
    private String result;

    @JSONField(name = "w_f_lsh")
    private String reportId;

}
