package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "确认违章车辆信息")
public class AffirmBreakRulesCarInfo {

    @ApiModelProperty(value = "违章车辆信息")
    private UserCar userCar;

    @ApiModelProperty(value = "违章驾驶证信息")
    private UserDriverLicense userDriverLicense;
}
