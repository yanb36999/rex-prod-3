package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.GenericEntity;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "交警处理结果")
@Deprecated
public class DisposeResult extends SimpleGenericEntity<String> {
    //业务ID|决定书编号|违法序号|执行结果|备注|处理人|处理时间|处理机关|复议机关|
    @ApiModelProperty(value = "业务Id", required = true)
    private String businessId;

    @ApiModelProperty(value = "决定书编号")
    private String decisionNumber;

    @ApiModelProperty(value = "违法序号")
    private String illegalNumber;

    @ApiModelProperty(value = "执行结果")
    private String execResult;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "处理人")
    private String handler;

    @ApiModelProperty(value = "处理时间")
    private String disposeDate;

    @ApiModelProperty(value = "处理机关")
    private String disposeOffice;

    @ApiModelProperty(value = "复议机关")
    private String againOffice;

    @ApiModelProperty(value = "文件原始内容",hidden = true)
    private String fileContent;

    @ApiModelProperty(value = "文件名",hidden = true)
    private String fileName;

    @ApiModelProperty(value = "存入时间",hidden = true)
    private Date updateTime;

}
