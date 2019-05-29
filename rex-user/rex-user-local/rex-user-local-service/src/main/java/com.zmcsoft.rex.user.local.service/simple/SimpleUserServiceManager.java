package com.zmcsoft.rex.user.local.service.simple;

import com.zmcsoft.rex.api.user.service.*;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 * @since
 */
@Service
@UseDataSource("userSysDataSource")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SimpleUserServiceManager implements UserServiceManager {
    @Autowired
    private UserCarService userCarService;

    @Autowired(required = false)
    private UserPointService userPointService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDriverLicenseService userDriverLicenseService;

    @Autowired
    private CarIllegalService carIllegalService;

    @Override
    public UserCarService userCarService() {
        return userCarService;
    }

    @Override
    public UserPointService userPointService() {
        return userPointService;
    }

    @Override
    public UserService userService() {
        return userService;
    }

    @Override
    public UserDriverLicenseService userDriverLicenseService() {
        return userDriverLicenseService;
    }

    @Override
    public CarIllegalService carIllegalService() {
        return carIllegalService;
    }
}
