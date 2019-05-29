package com.zmcsoft.rex.user.local.service.simple;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.service.UserCarService;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfo;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoDetailRequest;
import com.zmcsoft.rex.tmb.illegal.service.CarInfoService;
import com.zmcsoft.rex.user.local.dao.UserCarDao;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.hswebframework.web.service.DefaultDSLUpdateService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("userCarService")
@UseDataSource("userSysDataSource")
public class SimpleUserCarService extends GenericEntityService<UserCar, String> implements UserCarService {

    @Autowired
    private UserCarDao userCarDao;

    @Autowired
    private CarInfoService carInfoService;

    public UserCar setPropertyFromTmb(UserCar userCar) {
        if (null == userCar) {
            return null;
        }
        //TODO
//        if (userCar.getPlateNumber().equalsIgnoreCase("AW902N")) {
//            userCar.setPlateNumber("A22DC0");
//        } else if (userCar.getPlateNumber().equalsIgnoreCase("AQ96Y3")) {
//            userCar.setPlateNumber("AN82V6");
//        }
        try {


            CarInfo carInfo = carInfoService.carInfoDetail(CarInfoDetailRequest.builder()
                    .plateNumber(userCar.getPlateNumber())
                    .plateType(userCar.getPlateType())
                    .build());

            if (carInfo == null) {
                logger.warn("未能从交管局获取车辆信息:{} {}", userCar.getPlateNumber(), userCar.getPlateType());
                return userCar;
            }

            userCar.setBrand(carInfo.getVehicleBrand());
            userCar.setModel(carInfo.getCarModel());
            userCar.setCarOwner(carInfo.getOwner());
            userCar.setCarType(carInfo.getVehicleType());
            userCar.setCarStatus(carInfo.getStatus());
            userCar.setFrameNumber(carInfo.getVin());
            userCar.setColor(carInfo.getVehicleColor());
        }catch (Exception e){
            logger.error("从交管局获取车辆信息失败:{} {}", userCar.getPlateNumber(), userCar.getPlateType(),e);
        }
        return userCar;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<UserCar> getByUserId(String id) {
        return DefaultDSLQueryService
                .createQuery(userCarDao)
                .where("userId", id)
                .listNoPaging()
                .parallelStream()
                .peek(this::setPropertyFromTmb)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean changeUserCarStatus(String id, Byte status) {
        int i = DefaultDSLUpdateService
                .createUpdate(userCarDao)
                .where("id", id)
                .set("status", "0")
                .exec();
        return i != 0;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public UserCar selectByPlateNumberAndPlateType(String plateNumber, String plateType) {
        return setPropertyFromTmb(createQuery().where()
                .is("plateNumber", plateNumber).and()
                .is("plateType", plateType).single());
    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return null;
    }

    @Override
    public UserCarDao getDao() {
        return userCarDao;
    }
}
