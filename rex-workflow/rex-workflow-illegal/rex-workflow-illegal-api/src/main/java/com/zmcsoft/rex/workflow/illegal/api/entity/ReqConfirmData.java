package com.zmcsoft.rex.workflow.illegal.api.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ApiModel(description = "用户确认处理需要提交的数据")
@AllArgsConstructor
@NoArgsConstructor
public class ReqConfirmData {

    @ApiModelProperty(value = "举报Id")
    @NotBlank
    private String reportId;

    @ApiModelProperty(value = "违法车辆ID")
    @NotBlank
    private String carId;

    @ApiModelProperty(value = "号牌种类")
    @NotBlank
    private String plateType;

    @ApiModelProperty(value = "号牌号码")
    @NotBlank
    private String plateNo;

    @ApiModelProperty(value = "举报确认时间",dataType = "Date", required = true, example = "2017-10-19 10:05:00")
    @NotBlank
    private Date confirmTime;

    @ApiModelProperty(value = "驾驶证号码")
    @NotBlank
    private String licenseNo;

}
