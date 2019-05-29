package com.zmcsoft.rex.api.user.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;

/**
 * 用户驾照信息
 *
 * @author zhouhao
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDriverLicense extends SimpleGenericEntity<String> {

    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "身份证号码")
    private String idCard;
    @ApiModelProperty(value = "档案编号", required = true)
    private String fileNumber;
    @ApiModelProperty(value = "有效期始")
    private Date   startValidDate;

    @ApiModelProperty(value = "有效期止")
    private Date   endValidDate;

    @ApiModelProperty(value = "发证机关")
    private String sendOffice;

    @ApiModelProperty(value = "登记住所行政区划")
    private String registerSite;

    @ApiModelProperty(value = "联系住所行政区划")
    private String contactSite;

    @ApiModelProperty(value = "手机号码")
    private String telephone;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "驾驶证姓名", required = true)
    private String driverName;

    @ApiModelProperty(value = "驾驶证号码", required = true)
    private String licenseNumber;

    @ApiModelProperty(value = "驾驶证状态", required = true)
    private String licenseStatus;

    @ApiModelProperty(value = "准驾车型", required = true)
    private String drivingModel;

    @ApiModelProperty(value = "累计记分", required = true)
    private Integer totalScore;

    @ApiModelProperty(value = "初次领证日期")
    private Date firstDate;

    @ApiModelProperty(value = "状态详情")
    private String statusName;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Byte status;
}
