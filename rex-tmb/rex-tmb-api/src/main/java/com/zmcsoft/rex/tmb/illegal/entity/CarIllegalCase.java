package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "车辆违法案件")
public class CarIllegalCase {

    @ApiModelProperty(value = "案件ID", required = true)
    private String id;

    @ApiModelProperty(value = "违法时间", dataType = "Date", required = true, example = "2017-10-19 10:05:00")
    private Date illegalTime;

    @ApiModelProperty(value = "违法地点", required = true)
    private String illegalAddress;

    @ApiModelProperty(value = "违法行为代码", required = true, example = "1302")
    private String illegalActivities;

    @ApiModelProperty(value = "违法行为", required = true)
    private String illegalActivitiesText;

    @ApiModelProperty(value = "罚款金额", required = true, example = "100")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "违法记分数", required = true, example = "3")
    private Integer score;

    @ApiModelProperty(value = "处罚状态", example = "0")
    private String handleSign;

    @ApiModelProperty(value = "违法编号", required = true)
    private String illegalNumber;

    @ApiModelProperty(value = "采集机关", required = true)
    private String organization;

    @ApiModelProperty(value = "告知人")
    private Nunciator nunciatorOne;

    @ApiModelProperty(value = "告知人2")
    private Nunciator nunciatorTow;

    @ApiModelProperty(value = "采集机关名称", required = true)
    private String organizationName;

    @ApiModelProperty(value = "号牌种类编码", required = true, example = "02")
    private String plateType;

    @ApiModelProperty(value = "号牌种类名称", required = true, example = "小型汽车")
    private String plateTypeText;

    @ApiModelProperty(value = "号牌号码", required = true)
    private String plateNumber;

    @ApiModelProperty(value = "驾驶证号", required = true)
    private String driverNumber;

    @ApiModelProperty(value = "档案编号", required = true)
    private String fileNumber;

    @ApiModelProperty(value = "当事人", required = true)
    private String party;

    @ApiModelProperty(value = "行政区划", required = true)
    private String administrativeDivision;

    @ApiModelProperty(value = "违法地点代码", required = true)
    private String illegalAddressNo;

    @ApiModelProperty(value = "处理时间", required = true)
    private Date processingTime;

    @ApiModelProperty(value = "交款标记", required = true)
    private String paySign;

    @ApiModelProperty(value = "更新时间", required = true)
    private Date updateTime;

    @ApiModelProperty(value = "入库时间", required = true)
    private Date storageTime;

    @ApiModelProperty(value = "车辆识别代码")
    private String carDistinguishCode;

    @ApiModelProperty(value = "信息来源")
    private Integer infoSource;

    @ApiModelProperty(value = "是否提交")
    private Integer isCommit;

    @ApiModelProperty(value = "交通方式")
    private String trafficMode;

    @ApiModelProperty(value = "发送机关")
    private String sendOutOrganization;

    @ApiModelProperty(value = "业务处理状态")
    private Byte businessStatus;

    @ApiModelProperty(value = "决定书编号")
    private String decisionNumber;

    @ApiModelProperty(value = "处理时间")
    private Date handlerTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "内网执行状态")
    private String execResult;
}