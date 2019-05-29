package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "发送给Ftp的申请信息")
public class SendFtpApply {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "提交时间")
    private String submitTime;

    @ApiModelProperty(value = "驾驶证号")
    private String driverNo;
}
