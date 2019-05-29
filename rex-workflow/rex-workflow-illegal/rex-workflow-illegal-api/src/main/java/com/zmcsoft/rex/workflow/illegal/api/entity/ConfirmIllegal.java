package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "确认违法信息")
public class ConfirmIllegal {

    @ApiModelProperty(value = "案件ID")
    private String illegalId;

    @ApiModelProperty(value = "确认时间",dataType = "Date", required = true, example = "2017-10-19 10:05:00")
    private Date confrimDate;
}
