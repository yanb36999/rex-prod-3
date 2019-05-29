package com.zmcsoft.rex.user.local.service.simple;


import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.api.user.service.UserDriverLicenseService;
import com.zmcsoft.rex.tmb.illegal.entity.DriverLicense;
import com.zmcsoft.rex.tmb.illegal.service.DriverLicenseService;
import com.zmcsoft.rex.user.local.dao.UserDriverLicenseDao;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.ezorm.core.dsl.Delete;
import org.hswebframework.web.commons.entity.param.DeleteParamEntity;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.hswebframework.web.service.DefaultDSLUpdateService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userDriverLicense")
@UseDataSource("userSysDataSource")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j(topic = "business.user.driver-license")
public class SimpleUserDriverLicenseService extends GenericEntityService<UserDriverLicense, String> implements UserDriverLicenseService {

    @Autowired
    private UserDriverLicenseDao userDriverLicenseDao;

    @Autowired
    private DriverLicenseService licenseService;

    public UserDriverLicense setPropertiesFromTmb(UserDriverLicense userDriverLicense) {
        if (null == userDriverLicense) {
            return null;
        }

        DriverLicense license = licenseService.driverDetail(userDriverLicense.getLicenseNumber());

        if (license == null) {
            log.warn("未能从交管局获取驾照信息:{}", userDriverLicense.getLicenseNumber());
            return userDriverLicense;
        }
        BeanUtils.copyProperties(license,userDriverLicense);
        userDriverLicense.setDriverName(license.getName());
        userDriverLicense.setDrivingModel(license.getDriverType());
        userDriverLicense.setLicenseStatus(license.getStatus());
        userDriverLicense.setTotalScore(license.getScore());
        userDriverLicense.setFileNumber(license.getFileNumber());
        return userDriverLicense;
    }

    @Override
    public UserDriverLicense getByUserId(String id) {
        return setPropertiesFromTmb(DefaultDSLQueryService
                .createQuery(userDriverLicenseDao)
                .where("userId", id)
                .single());
    }

    @Override
    public boolean changeUserDriverLicenseStatus(String id, Byte status) {
        int i = DefaultDSLUpdateService
                .createUpdate(userDriverLicenseDao)
                .where("userId", id)
                .set("status", status)
                .exec();
        return i != 0;
    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return null;
    }

    @Override
    public UserDriverLicenseDao getDao() {
        return userDriverLicenseDao;
    }
}
