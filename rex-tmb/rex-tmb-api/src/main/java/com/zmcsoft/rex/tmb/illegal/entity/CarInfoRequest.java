package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "所有车辆信息")
public class CarInfoRequest {

    @ApiModelProperty(value = "所有人")
    private String owner;

    @ApiModelProperty(value = "身份证明号码")
    private String idCard;

}
