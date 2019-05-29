package com.zmcsoft.rex.tmb.illegal.service;

import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalData;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCaseQueryEntity;
import com.zmcsoft.rex.tmb.illegal.entity.ConfirmInfo;

import java.util.List;

/**
 * 违法案件信息
 *
 * @author zhouhao
 * @since 1.0
 */
public interface IllegalCaseService {

    /**
     * 根据用户的车辆信息获取车辆的违法案件信息
     *
     * @param car 车辆实体,不能为null
     * @return 违法案件集合, 如果没有返回空集合而不是null
     */
    List<CarIllegalCase> getByUserCar(CarIllegalCaseQueryEntity car);

    /**
     * 根据案件id,获取案件详细信息
     * @return 案件详细信息, 如果不存在则返回null
     */
    CarIllegalCase getById(String caseId);

    /**
     * 确认案件
     * @return 确认是否成功
     */
    boolean confirm(List<ConfirmInfo> confirmInfo);


    CarIllegalData getDataByUserCar(CarIllegalCaseQueryEntity car);


}
