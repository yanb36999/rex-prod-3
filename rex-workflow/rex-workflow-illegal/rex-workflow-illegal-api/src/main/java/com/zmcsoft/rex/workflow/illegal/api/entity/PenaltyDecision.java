package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by zhouhao on 2017/10/29.
 */
@Getter
@Setter
@Builder
@ApiModel(description = "处罚决定书")
@AllArgsConstructor
@NoArgsConstructor
public class PenaltyDecision {

    @ApiModelProperty("驾照信息")
    private UserDriverLicense driverLicense;

    @ApiModelProperty("案件信息")
    private CarIllegalCase carIllegalCase;

    @ApiModelProperty("案件处理信息")
    private CarIllegalCaseHandle illegalCaseHandle;

}
