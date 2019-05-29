package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 案件确认信息
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "确认案件信息")
@ToString
public class ConfirmInfo {

    @ApiModelProperty(value = "业务Id", required = true)
    private String businessId;

    @ApiModelProperty(value = "案件信息ID", required = true)
    private String caseId;

    @ApiModelProperty(value = "确认时间", required = true, example = "2017-10-23 12:12:12")
    private Date confirmDate;

}
