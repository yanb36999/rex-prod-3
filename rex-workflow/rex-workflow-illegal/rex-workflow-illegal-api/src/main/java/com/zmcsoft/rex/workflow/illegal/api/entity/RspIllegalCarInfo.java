package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "车辆举报信息")
public class RspIllegalCarInfo {

    @ApiModelProperty(value = "举报违法车辆")
    private ReportIllegalCar reportIllegalCar;

    @ApiModelProperty(value = "举报信息")
    private ReportInfo reportInfo;

    @ApiModelProperty(value = "举报数据集合")
    private List<ReportInfo> reportInfoList;
}
