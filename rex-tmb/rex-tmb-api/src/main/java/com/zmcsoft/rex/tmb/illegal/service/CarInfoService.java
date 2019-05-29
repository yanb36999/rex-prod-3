package com.zmcsoft.rex.tmb.illegal.service;

import com.zmcsoft.rex.tmb.illegal.entity.CarInfo;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoDetailRequest;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoRequest;

import java.util.List;

/**
 * 车辆信息
 */
public interface CarInfoService {


    /**
     *根据车主信息查询所有车辆信息
     * @param carInfoRequest
     * @return
     */
    List<CarInfo> getCarInfo(CarInfoRequest carInfoRequest);


    /**
     * 根据车牌信息查询车辆信息详情
     * @param carInfoDetailRequest
     * @return
     */
    CarInfo carInfoDetail(CarInfoDetailRequest carInfoDetailRequest);
}
