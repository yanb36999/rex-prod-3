package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(description = "用户违法举报详情")
@AllArgsConstructor
@NoArgsConstructor
public class ReportInfo extends SimpleGenericEntity<String>{

    @ApiModelProperty(value = "举报流水号")
    private String reportId;

    @ApiModelProperty(value = "举报人姓名")
    private String name;

    @ApiModelProperty(value = "举报时间")
    private Date reportDate;

    @ApiModelProperty(value = "微信用户ID")
    private String openId;

    @ApiModelProperty(value = "举报人身份证号码")
    private String idNumber;

    @ApiModelProperty(value = "举报人联系电话")
    private String phone;

    @ApiModelProperty(value = "举报说明描述")
    private String describe;

    @ApiModelProperty(value = "举报地址")
    private String address;

    @ApiModelProperty(value = "举报类型")
    private String reportType;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "视频地址")
    private String videoUrl;

    @ApiModelProperty(value = "音频地址")
    private String radioUrl;

    @ApiModelProperty(value = "处理状态")
    private String dspStatus;

    @ApiModelProperty(value = "车网通数据状态")
    private Integer cwtStatus;

    @ApiModelProperty(value = "车网通数据状态类型")
    private String cwtStatusType;

}
