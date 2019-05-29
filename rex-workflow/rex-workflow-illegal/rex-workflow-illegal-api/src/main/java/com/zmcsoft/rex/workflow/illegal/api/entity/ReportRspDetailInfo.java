package com.zmcsoft.rex.workflow.illegal.api.entity;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Api(tags = "响应违法信息")
public class ReportRspDetailInfo {

    @ApiModelProperty(value = "举报违法车辆信息")
    private ReportIllegalCar reportIllegalCar;

    @ApiModelProperty(value = "举报违法详情")
    private List<ReportIllegalInfo> reportIllegalInfoList;
}
