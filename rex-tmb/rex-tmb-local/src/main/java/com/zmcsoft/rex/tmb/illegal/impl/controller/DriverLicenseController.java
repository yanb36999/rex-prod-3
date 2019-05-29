package com.zmcsoft.rex.tmb.illegal.impl.controller;

import com.zmcsoft.rex.tmb.illegal.entity.DriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.DriverLicenseScore;
import com.zmcsoft.rex.tmb.illegal.impl.service.LocalDriverLicenseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hswebframework.web.authorization.Permission;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@Transactional(rollbackFor = Throwable.class)
@RequestMapping("/driver-license")
@Authorize(permission = "driver-license")
@Api(tags = "驾照信息")
public class DriverLicenseController {


    private LocalDriverLicenseService localDriverLicenseScoreService;

    @Autowired
    public void setLocalDriverLicenseScoreService(LocalDriverLicenseService localDriverLicenseScoreService) {
        this.localDriverLicenseScoreService = localDriverLicenseScoreService;
    }

    @GetMapping("/score")
    @Authorize(action = Permission.ACTION_GET)
    @ApiOperation("获取驾照剩余分数")
    public Integer getScore(DriverLicenseScore driverLicenseScore) {
        return localDriverLicenseScoreService.getScore(driverLicenseScore);
    }

    @GetMapping("/{licenseNumber}")
    @Authorize(action = Permission.ACTION_GET)
    @ApiOperation("根据驾驶证号码获取驾驶证信息")
    public DriverLicense getLicenseByLicenseNumber(@ApiParam("驾驶证号") @PathVariable String licenseNumber) {
        return localDriverLicenseScoreService.driverDetail(licenseNumber);
    }
}
