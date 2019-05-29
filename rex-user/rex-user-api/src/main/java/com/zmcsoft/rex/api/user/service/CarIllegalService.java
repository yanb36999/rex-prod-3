package com.zmcsoft.rex.api.user.service;

import com.zmcsoft.rex.api.user.entity.CarIllegal;
import com.zmcsoft.rex.api.user.entity.UserCar;
import org.hswebframework.web.service.CrudService;

import java.util.List;

/**
 * @author zhouhao
 * @since 1.0
 */
public interface CarIllegalService extends CrudService<CarIllegal,String> {

    String insert(CarIllegal carIllegal);
}
