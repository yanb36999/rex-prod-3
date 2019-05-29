package com.zmcsoft.rex.workflow.illegal.api.entity;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;

/**
 * @author zhouhao
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "违法案件处理信息")
public class CarIllegalCaseHandle extends SimpleGenericEntity<String> {

    @ApiModelProperty("业务ID")
    private String businessId;

    @ApiModelProperty(value = "案件详情")
    private CarIllegalCase illegalCase;

    @ApiModelProperty(value = "车辆信息")
    private UserCar carInfo;

    @ApiModelProperty(value = "驾驶证信息")
    private UserDriverLicense driverLicense;

    @ApiModelProperty(value = "处理时间")
    private Date handleTime;

    @ApiModelProperty(value = "处理状态")
    private Byte handleStatus;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "确认时间")
    private Date confirmTime;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "处理渠道")
    private String handleChannel;

    //决定书编号，执行结果，备注，处理人，处理机关，复议机关

    @ApiModelProperty(value = "决定书编号")
    private String decisionNumber;

    @ApiModelProperty(value = "执行结果")
    private String execResult;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "处理人")
    private String handler;

    @ApiModelProperty(value = "处理机关")
    private String disposeOffice;

    @ApiModelProperty(value = "复议机关")
    private String againOffice;

    @ApiModelProperty(value = "文件原始内容",hidden = true)
    private String fileContent;

    @ApiModelProperty(value = "文件名",hidden = true)
    private String fileName;

    @ApiModelProperty(value = "累计记分")
    private Integer licenseScore;


}
