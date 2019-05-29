package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;

/**
 * 业务日志
 *
 * @author zhouhao
 * @since 1.0
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "案件处理记录")
public class IllegalCaseHistory extends SimpleGenericEntity<String> {

    @ApiModelProperty("已弃用")
    @Deprecated
    private String key;

    @ApiModelProperty("已弃用")
    @Deprecated
    private String keyText;

    @ApiModelProperty("操作事项")
    private String action;

    @ApiModelProperty("操作事项名称")
    private String actionText;

    @ApiModelProperty("案件ID")
    private String caseId;

    @ApiModelProperty("备注")
    private String comment;

    @ApiModelProperty("详情")
    private String detail;

    @ApiModelProperty("状态")
    private Byte status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人主键")
    private String creatorId;

    @ApiModelProperty("创建人名称")
    private String creatorName;
}
