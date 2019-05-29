package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@ApiModel(description = "用户车辆违法详情")
public class UserCarIllegalDetail {

    @ApiModelProperty(value = "用户信息", required = true)
    protected User user;

    @ApiModelProperty(value = "车辆违法处理信息")
    private CarIllegalCaseHandle carIllegalCaseHandle;

    @ApiModelProperty(value = "处罚记录")
    private List<IllegalCaseHistory> illegalCaseHistories;
}
