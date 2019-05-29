package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author zhouhao
 * @since 1.0
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "告知人")
public class Nunciator {

    @ApiModelProperty(value = "告知人姓名")
    private String name;

    @ApiModelProperty(value = "告知人单位名称")
    private String orgName;
}
