package com.zmcsoft.rex.api.user.service;


/**
 * 用户相关服务集中管理
 * @author zhouhao
 * @since 1.0
 */
public interface UserServiceManager {

    /**
     *
     * @return 用户车辆服务
     */
    UserCarService userCarService();

    /**
     *
     * @return 用户积分服务
     */
    UserPointService userPointService();

    /**
     *
     * @return 用户信息服务
     */
    UserService userService();

    /**
     *
     * @return 用户驾照服务
     */
    UserDriverLicenseService userDriverLicenseService();


    CarIllegalService carIllegalService();

}
