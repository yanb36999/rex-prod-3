package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "驾照详情")
public class DriverLicense {


    @ApiModelProperty(value = "身份证号码")
    private String idCard;

    @ApiModelProperty(value = "档案编号",required = true)
    private String fileNumber;

    @ApiModelProperty(value = "准驾车型")
    private String driverType;

    @ApiModelProperty(value = "初次领证日期")
    private Date firstDate;

    @ApiModelProperty(value = "有效期始")
    private Date startValidDate;

    @ApiModelProperty(value = "有效期止")
    private Date endValidDate;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "身份证名称")
    private String identityName;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "审验有效期止")
    private Date checkDate;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "不知道的字段")
    private String jzqx;

    @ApiModelProperty(value = "提交日期")
    private Date commitDate;

    @ApiModelProperty(value = "清分日期")
    private Date resetScoreDate;

    @ApiModelProperty(value = "发证机关")
    private String sendOffice;

    @ApiModelProperty(value = "是否提交")
    private Integer commit;

    @ApiModelProperty(value = "入库时间")
    private Date intoDate;

    @ApiModelProperty(value = "累计计分")
    private Integer score;

    @ApiModelProperty(value = "手机号码")
    private String telephone;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "登记住所行政区划")
    private String registerSite;

    @ApiModelProperty(value = "联系住所行政区划")
    private String contactSite;

}
