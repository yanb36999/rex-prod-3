package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.UserCar;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "响应给前端的违法签收列表")
public class RspSignIllegalInfo {

    private UserCar userCar;

    private List<ReportIllegalInfo> reportIllegalInfoList;
}
