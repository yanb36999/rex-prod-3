package com.zmcsoft.rex.workflow.illegal.api.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "确认违章信息详情")
@Deprecated
public class BreakRulesInfo {


    @ApiModelProperty(value = "可处理计分")
    private Integer totalScore;

    @ApiModelProperty(value = "车牌号")
    private Integer plateNumber;

    @ApiModelProperty(value = "未处理笔数")
    private String noDispose;

    @ApiModelProperty(value = "违法时间")
    private Date breakDate;

    @ApiModelProperty(value = "违法地点")
    private String site;

    @ApiModelProperty(value = "违法行为")
    private String behavior;

    @ApiModelProperty(value = "罚款金额")
    private String fineMoney;

    @ApiModelProperty(value = "记分")
    private String score;
}
