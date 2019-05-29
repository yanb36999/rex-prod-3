package com.zmcsoft.rex.tmb.illegal.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 车辆信息
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "车辆信息")
public class CarInfo {


    @ApiModelProperty(value = "号牌种类",required = true)
    private String plateType;

    @ApiModelProperty(value = "号牌号码",required = true)
    private String plateNumber;

    @ApiModelProperty(value = "车辆型号")
    private String carModel;

    @ApiModelProperty(value = "车辆识别代号")
    private String vin;

    @ApiModelProperty(value = "发动机号码")
    private String engineNo;

    @ApiModelProperty(value = "初次登记日期")
    private Date registerDate;

    @ApiModelProperty(value = "有效期止")
    private Date validDate;

    @ApiModelProperty(value = "强制报废期止")
    private Date scrapDate;

    @ApiModelProperty(value = "档案编号")
    private String fileNumber;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "抵押标记")
    private String pledgeSign;

    @ApiModelProperty(value = "抵押解除日期")
    private Date pledgeRelieveDate;

    @ApiModelProperty(value = "抵押日期")
    private Date pledgeDate;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "发证机关(全称)")
    private String certificate;

    @ApiModelProperty(value = "所有人")
    private String owner;

    @ApiModelProperty(value = "身份证明号码")
    private String idCard;

    @ApiModelProperty(value = "是否统计")
    private Integer statistics;

    @ApiModelProperty(value = "入库时间")
    private Date putStorageDate;

    @ApiModelProperty(value = "序号")
    private String id;

    @ApiModelProperty(value = "使用性质")
    private String useNature;

    @ApiModelProperty(value = "车辆类型")
    private String vehicleType;

    @ApiModelProperty(value = "核定载客人数")
    private Integer seatingCapacity;

    @ApiModelProperty(value = "出场日期")
    private Date productionDate;

    @ApiModelProperty(value = "同步文件名")
    private String syncFileName;

    @ApiModelProperty(value = "燃料种类")
    private String eldingType;

    @ApiModelProperty(value = "号牌号码2,带川字")
    private String plateNumber2;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "住所详细地址")
    private String address;

    @ApiModelProperty(value = "手机号码")
    private String mobilePhone;

    @ApiModelProperty(value = "住所行政区划")
    private String addressDistrict;

    @ApiModelProperty(value = "所有权")
    private String ownership;

    @ApiModelProperty(value = "行政区划")
    private String district;

    @ApiModelProperty(value = "车辆品牌")
    private String vehicleBrand;

    @ApiModelProperty(value = "车辆颜色")
    private String vehicleColor;

    @ApiModelProperty(value = "是否新能源")
    private String newEnergy;

}
