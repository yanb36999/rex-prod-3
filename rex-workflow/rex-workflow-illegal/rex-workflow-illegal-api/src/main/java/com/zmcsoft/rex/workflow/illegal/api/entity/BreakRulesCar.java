package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.UserCar;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "违法处罚车辆")
@Deprecated
public class BreakRulesCar{

    @ApiModelProperty(value = "车辆信息")
    private UserCar userCar;

    @ApiModelProperty(value = "未处理笔数")
    private Integer noDispose;

    @ApiModelProperty(value = "已处理笔数")
    private Integer dispose;

    @ApiModelProperty(value = "已处理未交款")
    private Integer disposeNoPay;


}
