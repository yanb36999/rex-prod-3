package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * @author zhouhao
 * @since
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "告知人详情")
public class NunciatorInfo {

    @ApiModelProperty("告知人单位编码")
    private String orgCode;

    @ApiModelProperty("行政区划名称")
    private String districtName;

    @ApiModelProperty("告知单位名称")
    private String orgName;

    @ApiModelProperty("告知人")
    private String name;

    @ApiModelProperty("告知人警号")
    private String policeCode;

    @ApiModelProperty("告知人2")
    private String name2;

    @ApiModelProperty("告知人警号2")
    private String policeCode2;

    @ApiModelProperty(value = "有效期始", example = "2012-01-25")
    private Date startWith;

    @ApiModelProperty(value = "有效期始", example = "2022-01-01")
    private Date endWith;

    @ApiModelProperty(value="联系地址",example = "成都市高新区科园一路6号")
    private String address;

    @ApiModelProperty(value="联系电话",example = "88888888")
    private String phone;

    @ApiModelProperty(value = "复议机关",example = "成都市公安局交通管理局")
    private String againOffice;

    @ApiModelProperty(value="告知单位全称",example = "88888888")
    private String fullName;

    @ApiModelProperty(value="告知法院",example = "")
    private String court;
}
