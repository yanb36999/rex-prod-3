package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(description = "违法车辆信息")
@AllArgsConstructor
@NoArgsConstructor
public class ReportIllegalCar extends SimpleGenericEntity<String> {

    @ApiModelProperty(value = "号牌种类")
    private String plateType;

    @ApiModelProperty(value = "号牌种类中文")
    private String plateTypeText;

    @ApiModelProperty(value = "号牌号码")
    private String plateNo;

    @ApiModelProperty(value = "车辆类型")
    private String carType;

    @ApiModelProperty(value = "机动车状态")
    private String carStatus;

    @ApiModelProperty(value = "机动车所有人")
    private String owner;

    @ApiModelProperty(value = "身份证号码")
    private String idNo;

    @ApiModelProperty(value = "档案编号")
    private String fileNumber;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "手机号码")
    private String phoneNo;

    @ApiModelProperty(value = "燃料种类")
    private String fuelType;

    @ApiModelProperty(value = "车辆颜色")
    private String carColor;

    @ApiModelProperty(value = "车辆品牌")
    private String carBrand;

    @ApiModelProperty(value = "是否新能源")
    private String newEnergy;

    @ApiModelProperty(value = "举报流水号")
    private String reportId;

    @ApiModelProperty(value = "视频地址")
    private String videoPath;

    @ApiModelProperty(value = "视频截图地址")
    private List<String> videoShotPath;

    @ApiModelProperty(value = "签收部门")
    private String signDept;

    @ApiModelProperty(value = "签收部门名称")
    private String signDeptName;

    @ApiModelProperty(value = "签收部门全称")
    private String signDeptFullName;

    @ApiModelProperty(value = "签收复核人员编码")
    private String signReviewer;

    @ApiModelProperty(value = "签收复核人员姓名")
    private String signReviewerName;

    @ApiModelProperty(value = "签收复核状态")
    private String signReviewStatus;


    @ApiModelProperty(value = "处罚录入部门")
    private String punishSignDept;

    @ApiModelProperty(value = "处罚录入部门名称")
    private String punishSignDeptName;

    @ApiModelProperty(value = "处罚录入状态（0、未处罚录入，1、已处罚录入，2、未处罚完毕）")
    private String punishSignStatus;

    @ApiModelProperty(value = "处罚录入人")
    private String punishSignPerson;

    @ApiModelProperty(value = "处罚录入人姓名")
    private String punishSignName;


    @ApiModelProperty(value = "处罚结果(1、违法处罚;2、案件终止)")
    private String punishResult;

    @ApiModelProperty(value = "案件终止原因(不复核条件原因)")
    private String stopCause;

    @ApiModelProperty(value = "违法笔数")
    private Integer illegalCount;

    @ApiModelProperty(value = "短信告知时间")
    private Date msgSendDate;

    @ApiModelProperty(value = "短信告知状态：1、已推送；2、已成功")
    private Byte msgSendStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "举报信息")
    private List<ReportInfo> reportInfo;

    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;

    @ApiModelProperty(value = "当前处理状态")
    private String dspStatus;

    @ApiModelProperty(value = "当前处理状态名称")
    private String dspStatusName;

    @ApiModelProperty(value = "车网通数据状态")
    private Integer cwtStatus;

    @ApiModelProperty(value = "车网通数据状态类型")
    private String cwtStatusType;

}
