package com.zmcsoft.rex.api.user.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;

/**
 * 用户车辆信息
 *
 * @author zhouhao
 * @see User
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarIllegal extends SimpleGenericEntity<String> {

    /**
     * id
     *
     * @see
     */
    private String id;

    @ApiModelProperty(value = "流水号")
    private String illegalId;

    @ApiModelProperty(value = "机动车号牌种类", required = true)
    private String plateType;

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号", required = true)
    private String plateNumber;

    @ApiModelProperty(value = "所有人")
    private String owner;


    @ApiModelProperty(value = "联系电话")
    private String phone;


    @ApiModelProperty(value = "违法时间")
    private Date illegalTime;

    @ApiModelProperty(value = "违法地点", required = true)
    private String illegalAddress;

    @ApiModelProperty(value = "违法行为内容")
    private String illegalBehaviorName;

    @ApiModelProperty(value = "复核部门")
    private String signDept;

    @ApiModelProperty(value = "复核部门名称")
    private String signDeptName;

    @ApiModelProperty(value = "复核人员姓名")
    private String signReviewerName;

    @ApiModelProperty(value = "复核时间")
    private Date createTime;

    @ApiModelProperty(value = "车辆品牌")
    private String carBrand;

    @ApiModelProperty(value = "处置要求")
    private String text;

    @ApiModelProperty(value = "有效状态")
    private Byte status;

    @ApiModelProperty(value = "发现机关")
    private String reportDept;

    @ApiModelProperty(value = "发现机关名称")
    private String reportDeptName;

    @ApiModelProperty(value = "发现人")
    private String reportName;

    @ApiModelProperty(value = "发现时间")
    private Date reportDate;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "同步状态")
    private Byte synStatus;




}
