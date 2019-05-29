package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.workflow.illegal.api.ReportStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel(description = "响应数据车辆+举报数据")
@AllArgsConstructor
@NoArgsConstructor
public class RspReportCarData {

    @ApiModelProperty(value = "用户车辆")
    private UserCar userCar;

    @ApiModelProperty(value = "举报数据")
    private List<ReportInfo> reportInfoList;


}
