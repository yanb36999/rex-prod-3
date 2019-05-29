package com.zmcsoft.rex.workflow.illegal.api.entity;

import org.hswebframework.web.commons.entity.SimpleGenericEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 违法举报日志
 *
 * @author hsweb-generator
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "违法举报日志")
public class IllegalReportLog extends SimpleGenericEntity<String> {

    @ApiModelProperty(value = "举报id")
    private String reportId;

    @ApiModelProperty(value = "管理部门编码")
    private String orgCode;

    @ApiModelProperty(value = "管理部门名称")
    private String orgName;

    @ApiModelProperty(value = "经办人id")
    private String handlerId;

    @ApiModelProperty(value = "经办人姓名")
    private String handlerName;

    @ApiModelProperty(value = "处理时间")
    private java.util.Date handleTime;

    @ApiModelProperty(value = "处理状态")
    private Integer status;

    @ApiModelProperty(value = "处理意见")
    private String suggestion;

    @ApiModelProperty(value = "不通过原因")
    private String reason;

    @ApiModelProperty(value = "不通过原因代码")
    private String reasonCode;

    @ApiModelProperty(value = "备注")
    private String remark;
}