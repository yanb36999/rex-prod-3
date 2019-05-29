package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "车辆违法案件列表")
public class CarIllegalCaseDetail {

    @ApiModelProperty("车辆信息")
    private UserCar car;

    @ApiModelProperty(value = "车辆违法信息")
    private List<CarIllegalCase> illegalCaseList;

    @ApiModelProperty(value = "未处理笔数")
    private Integer noDispose;

    @ApiModelProperty(value = "已处理笔数")
    private Integer dispose;

    @ApiModelProperty(value = "已处理未交款")
    private Integer disposeNoPay;

    @ApiModelProperty(value = "处理状态统计")
    private List<HandleStatus.HandleStatusCount> handleStatus;
}
