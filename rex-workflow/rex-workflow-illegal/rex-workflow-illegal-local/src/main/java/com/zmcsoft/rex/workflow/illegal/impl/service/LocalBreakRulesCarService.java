package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.service.UserServiceManager;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCaseQueryEntity;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalData;
import com.zmcsoft.rex.tmb.illegal.service.IllegalCaseService;
import com.zmcsoft.rex.workflow.illegal.api.entity.BreakRulesCar;
import com.zmcsoft.rex.workflow.illegal.api.service.BreakRulesCarService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class LocalBreakRulesCarService implements BreakRulesCarService {


    private UserServiceManager userServiceManager;

    private IllegalCaseService illegalCaseService;

    @Autowired
    public void setUserServiceManager(UserServiceManager userServiceManager) {
        this.userServiceManager = userServiceManager;
    }

    @Override
    public List<BreakRulesCar> getByUserId(String userId) {
        //根据用户ID查询车辆信息
        List<UserCar> carList = userServiceManager.userCarService().getByUserId(userId);

        //根据车辆信息查询违法数据
        return carList.parallelStream().map(userCar -> {
            CarIllegalData data = illegalCaseService.getDataByUserCar((CarIllegalCaseQueryEntity.builder()
                    .plateNumber(userCar.getPlateNumber())
                    .plateType(userCar.getPlateType())
                    .build()));
            return BreakRulesCar.builder()
                    .userCar(userCar)
                    .dispose(data.getDispose())
                    .noDispose(data.getNoDispose())
                    .disposeNoPay(data.getDisposeNoPay()).build();

        }).collect(Collectors.toList());
    }

}
