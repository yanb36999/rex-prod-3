package com.zmcsoft.rex.api.user.service;

import com.zmcsoft.rex.api.user.entity.UserCar;
import org.hswebframework.web.service.CrudService;

import java.util.List;

/**
 * @author zhouhao
 * @since 1.0
 */
public interface UserCarService extends CrudService<UserCar,String> {
    List<UserCar> getByUserId(String id);

    String saveOrUpdate(UserCar userCar);

    boolean changeUserCarStatus(String id, Byte status);

    UserCar selectByPlateNumberAndPlateType(String plateNumber, String plateType);
}
