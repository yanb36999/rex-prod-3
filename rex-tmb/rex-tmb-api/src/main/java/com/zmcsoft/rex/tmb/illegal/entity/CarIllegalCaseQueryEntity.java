package com.zmcsoft.rex.tmb.illegal.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "案件查询参数")
public class CarIllegalCaseQueryEntity {
    @NotBlank
    @ApiModelProperty("车辆号牌类型")
    private String plateType;

    @NotBlank
    @ApiModelProperty("车辆号牌")
    private String plateNumber;

    @ApiModelProperty("是否只查询未处理的案件")
    private boolean newIllegal;
}
