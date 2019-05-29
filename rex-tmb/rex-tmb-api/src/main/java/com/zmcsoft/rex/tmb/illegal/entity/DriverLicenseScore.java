package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "驾照可处理分数查询")
public class DriverLicenseScore {

    @ApiModelProperty(value = "姓名", required = true)
    private String name;

    @ApiModelProperty(value = "驾驶证号", required = true)
    private String driverNumber;

    @ApiModelProperty(value = "档案编号", required = true)
    private String fileNumber;

    @ApiModelProperty(value = "可处理分数")
    private Integer score;
}
