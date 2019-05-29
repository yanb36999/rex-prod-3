package com.zmcsoft.rex.tmb.illegal.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "车辆详情")
public class CarInfoDetailRequest {

    @ApiModelProperty(value = "号牌种类",required = true)
    private String plateType;

    @ApiModelProperty(value = "号牌号码",required = true)
    private String plateNumber;
}
