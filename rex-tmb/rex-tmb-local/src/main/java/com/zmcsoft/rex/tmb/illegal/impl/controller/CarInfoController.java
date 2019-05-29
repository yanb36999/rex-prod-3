package com.zmcsoft.rex.tmb.illegal.impl.controller;


import com.zmcsoft.rex.tmb.illegal.entity.CarInfo;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoDetailRequest;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoRequest;
import com.zmcsoft.rex.tmb.illegal.impl.service.LocalCarInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hswebframework.web.authorization.Permission;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@RestController
@Transactional(rollbackFor = Throwable.class)
@RequestMapping("/car")
@Authorize(permission = "car-info")
@Api(tags = "车辆信息")
public class CarInfoController {

    private LocalCarInfoService carInfoService;

    @Autowired
    public void setCarInfoService(LocalCarInfoService carInfoService) {
        this.carInfoService = carInfoService;
    }

    /**
     * 根据车辆所有者信息查询所有车辆集合
     * @param carInfoRequest
     * @return
     */
    @GetMapping("/info")
    @Authorize(action = Permission.ACTION_GET)
    @ApiOperation("根据所有人和身份证获取车辆信息")
    public List<CarInfo> getCarInfoByOwner(CarInfoRequest carInfoRequest) {
        return carInfoService.getCarInfo(carInfoRequest);
    }

    @GetMapping("/detail")
    @Authorize(action = Permission.ACTION_GET)
    @ApiOperation("根据号牌种类和号牌号码获取车辆信息")
    public CarInfo carInfoDetail(CarInfoDetailRequest carInfoDetailRequest){
        return carInfoService.carInfoDetail(carInfoDetailRequest);
    }
}
