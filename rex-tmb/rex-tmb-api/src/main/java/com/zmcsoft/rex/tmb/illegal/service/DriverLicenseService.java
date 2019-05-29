package com.zmcsoft.rex.tmb.illegal.service;

import com.zmcsoft.rex.tmb.illegal.entity.DriverLicenseScore;
import com.zmcsoft.rex.tmb.illegal.entity.DriverLicense;

/**
 * 查询驾照信息
 */
public interface DriverLicenseService {

    /**
     * 查询累计计分数
     * @param driverLicenseScore
     * 驾照号,姓名,档案编号
     * @return
     */
    Integer getScore(DriverLicenseScore driverLicenseScore);


    /**
     * 根据驾驶证号查询驾驶证详细信息
     * @param idCard
     * @return
     */
    DriverLicense driverDetail(String idCard);
}
