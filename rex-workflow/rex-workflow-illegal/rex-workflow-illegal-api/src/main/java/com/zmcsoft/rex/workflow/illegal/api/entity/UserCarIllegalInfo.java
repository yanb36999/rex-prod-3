package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.User;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(description = "用户车辆违法信息")
public class UserCarIllegalInfo {

    @ApiModelProperty(value = "用户信息", required = true)
    protected User user;

    @ApiModelProperty(value = "用户驾驶证信息", required = true)
    private UserDriverLicense driverLicense;

    @ApiModelProperty(value = "案件详情列表")
    private List<CarIllegalCaseDetail> caseDetailList;
}
