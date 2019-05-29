package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "违法处罚车辆违章笔数统计")
public class CarIllegalData {

    @ApiModelProperty(value = "未处理笔数")
    private Integer noDispose;

    @ApiModelProperty(value = "已处理笔数")
    private Integer dispose;

    @ApiModelProperty(value = "已处理未交款")
    private Integer disposeNoPay;

}
