package com.zmcsoft.rex.api.user.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hswebframework.web.commons.entity.SimpleGenericEntity;

import java.util.Date;

/**
 * 用户车辆信息
 *
 * @author zhouhao
 * @see User
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCar extends SimpleGenericEntity<String> {

    /**
     * 用户Id
     *
     * @see User#id
     */
    private String userId;

    @ApiModelProperty(value = "机动车号牌种类", required = true)
    private String plateType;

    @ApiModelProperty(value = "号牌种类名称", required = true, example = "小型汽车")
    private String plateTypeText;

    @ApiModelProperty(value = "机动车车辆品牌（类型）", required = true)
    private String carType;

    @ApiModelProperty(value = "机动车状态", required = true)
    private String carStatus;

    @ApiModelProperty(value = "机动车所有人", required = true)
    private String carOwner;

    @ApiModelProperty(value = "身份证明号码")
    private String idCard;

    @ApiModelProperty(value = "使用性质")
    private String useNature;

    @ApiModelProperty(value = "档案编号")
    private String fileNumber;

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号", required = true)
    private String plateNumber;

    /**
     * 车架号
     */
    @ApiModelProperty(value = "车架号", required = true)
    // TODO: 2017/11/6 现在为车辆识别代码
    private String frameNumber;

    @ApiModelProperty(value = "行政区划")
    private String district;

    @ApiModelProperty(value = "住所行政区划")
    private String addressDistrict;

    @ApiModelProperty(value = "所有权")
    private String ownership;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "住所详细地址")
    private String address;

    @ApiModelProperty(value = "手机号码")
    private String mobilePhone;

    @ApiModelProperty(value = "核定载客人数")
    private Integer seatingCapacity;

    @ApiModelProperty(value = "出场日期")
    private Date productionDate;

    @ApiModelProperty(value = "同步文件名")
    private String syncFileName;

    @ApiModelProperty(value = "燃料种类")
    private String eldingType;

    @ApiModelProperty(value = "是否新能源")
    private String newEnergy;

    /**
     * 车辆品牌
     */
    @ApiModelProperty(value = "车辆品牌")
    private String brand;

    /**
     * 车辆型号
     */
    @ApiModelProperty(value = "车辆型号")
    private String model;

    /**
     * 车辆颜色
     */
    @ApiModelProperty(value = "车辆颜色")
    private String color;

    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    private Date createTime;

    /**
     * 上一次修改时间
     */
    @ApiModelProperty(hidden = true)
    private Date updateTime;

    /**
     * 状态
     */
    @ApiModelProperty(hidden = true)
    private Byte status;
}
