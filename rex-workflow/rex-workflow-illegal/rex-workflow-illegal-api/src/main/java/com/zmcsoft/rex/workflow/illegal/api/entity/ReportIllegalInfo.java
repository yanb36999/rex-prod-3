package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@Builder
@ApiModel(description = "违法信息表")
@AllArgsConstructor
@NoArgsConstructor
public class ReportIllegalInfo extends SimpleGenericEntity<String>{


    @ApiModelProperty(value = "决定书类别")
    private String decisionType;

    @ApiModelProperty(value = "决定书编号")
    private String decisionNo;

    @ApiModelProperty(value = "文书校验位")
    private String checkNo;

    @ApiModelProperty(value = "举报流水号")
    private String reportId;

    @ApiModelProperty(value = "人员分类")
    private Byte personType;

    @ApiModelProperty(value = "驾驶证号")
    private String driverNo;

    @ApiModelProperty(value = "档案编号")
    private String fileNo;

    @ApiModelProperty(value = "发证机关")
    private String sendLicenceOffice;

    @ApiModelProperty(value = "准驾车型")
    private String driverCarType;

    @ApiModelProperty(value = "当事人")
    private String parties;

    @ApiModelProperty(value = "住所行政区划")
    private String homeAdminDivision;

    @ApiModelProperty(value = "住所详细地址")
    private String detailSite;

    @ApiModelProperty(value = "联系方式")
    private String contactWay;

    @ApiModelProperty(value = "号牌种类")
    private String plateType;

    @ApiModelProperty(value = "号牌号码")
    private String plateNo;

    @ApiModelProperty(value = "违法时间")
    private Date illegaltime;

    @ApiModelProperty(value = "行政区划")
    private String adminDivision;

    @ApiModelProperty(value = "违法地点(道路编码)")
    private String illegalAddress;

    @ApiModelProperty(value = "违法地址(道路名称)")
    private String illegalSite;

    @ApiModelProperty(value = "路口路段编码")
    private String roadCode;

    @ApiModelProperty(value = "路口路段名称")
    private String roadName;

    @ApiModelProperty(value = "违法行为")
    private String illegalBehavior;

    @ApiModelProperty(value = "违法行为内容")
    private String illegalBehaviorName;

    @ApiModelProperty(value = "违法记分数")
    private Integer illegalScore;

    @ApiModelProperty(value = "罚款金额")
    private Integer illegalFine;

    @ApiModelProperty(value = "执勤民警")
    private String dutyPolice;

    @ApiModelProperty(value = "交款方式")
    private String payWay;

    @ApiModelProperty(value = "处理机关")
    private String dspOffice;

    @ApiModelProperty(value = "处理机关名称")
    private String dspOfficeName;

    @ApiModelProperty(value = "处理时间")
    private Date dspDate;

    @ApiModelProperty(value = "录入人")
    private String dataEntryClerk;

    @ApiModelProperty(value = "录入时间")
    private Date dataEntryTime;

    @ApiModelProperty(value = "经办人1")
    private String responsibleOne;

    @ApiModelProperty(value = "经办人2")
    private String responsibleTwo;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "处理状态")
    private String disposeStatus;

    @ApiModelProperty(value = "处理状态名称")
    private String dspStatusName;


    @ApiModelProperty(value = "违法信息来源(1、网上申请。2、现场处罚)")
    private String illegalInfoSource;

    @ApiModelProperty(value = "取消处罚原因")
    private String cancelCause;

    @ApiModelProperty(value = "取消处罚标识")
    private String cancelSign;
}
