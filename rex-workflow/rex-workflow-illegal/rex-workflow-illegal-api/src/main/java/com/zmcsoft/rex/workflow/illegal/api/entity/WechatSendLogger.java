package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "用户车辆违法信息")
public class WechatSendLogger extends SimpleGenericEntity<String> {

    @ApiModelProperty(value = "用户openid", required = true)
    private String openId;

    @ApiModelProperty(value = "推送结果")
    private String sendStatus;

    @ApiModelProperty(value = "推送内容")
    private String content;

    @ApiModelProperty(value = "关键字")
    private String keyword;

    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @ApiModelProperty(value = "推送返回消息")
    private String result;

    @ApiModelProperty(value = "推送时间")
    private Date createTime;

    @ApiModelProperty(value = "备注")
    private String comment;

}