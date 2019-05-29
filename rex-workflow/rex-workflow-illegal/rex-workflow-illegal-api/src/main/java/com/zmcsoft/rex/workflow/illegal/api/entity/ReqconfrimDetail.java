package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@ApiModel(description = "用户确认处理需要提交的数据")
@AllArgsConstructor
@NoArgsConstructor
public class ReqconfrimDetail extends ReqConfirmData{

    @ApiModelProperty(value = "确认违法信息")
    @NotBlank
    private List<ConfirmIllegal> confirmIllegals;

}
