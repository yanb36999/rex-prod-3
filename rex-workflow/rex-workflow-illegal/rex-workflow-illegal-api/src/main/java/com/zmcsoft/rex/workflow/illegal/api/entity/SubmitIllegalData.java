package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.Api;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Api(tags = "响应违法信息")
public class SubmitIllegalData {

    RspRerportInfo rspRerportInfo;
}

/**
 *
 *
 *  wf_report:
 *      --wf_car:
 *          ---wf_info:
 *          ---wf_apply:
 *
 *
 *
 * *
 */