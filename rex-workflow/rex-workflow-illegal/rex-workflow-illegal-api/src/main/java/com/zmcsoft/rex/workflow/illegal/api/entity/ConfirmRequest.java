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
@ToString
@ApiModel(description = "违法处罚车辆")
public class ConfirmRequest {

    @ApiModelProperty(value = "案件id",required = true)
    private String caseId;

    @ApiModelProperty(value = "确认时间", required = true,example = "2017-10-24 10:21:00")
    private Date confirmDate;

}
