package com.zmcsoft.rex.user.local.service.simple;

import com.zmcsoft.rex.api.user.entity.CarIllegal;
import com.zmcsoft.rex.api.user.service.CarIllegalService;
import com.zmcsoft.rex.user.local.dao.CarIllegalDao;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("carIllegalService")
@UseDataSource("userSysDataSource")
public class SimpleCarIllegalService extends GenericEntityService<CarIllegal, String> implements CarIllegalService {

    @Autowired
    private CarIllegalDao carIllegalDao;

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return null;
    }

    @Override
    public CarIllegalDao getDao() {
        return carIllegalDao;
    }
}
