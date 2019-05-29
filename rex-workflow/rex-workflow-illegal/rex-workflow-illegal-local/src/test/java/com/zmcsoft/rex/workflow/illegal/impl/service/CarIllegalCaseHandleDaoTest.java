package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.workflow.illegal.api.entity.CarIllegalCaseHandle;
import com.zmcsoft.rex.workflow.illegal.api.service.CarIllegalCaseHandleService;
import com.zmcsoft.rex.workflow.illegal.impl.dao.CarIllegalCaseHandleDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 * @since
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisDaoTestApplication.class)
public class CarIllegalCaseHandleDaoTest {

    @Autowired
    private CarIllegalCaseHandleDao carIllegalCaseHandleDao;



    @Test
    public void insertCarIllegalCaseHandle() {
        CarIllegalCaseHandle carIllegalCaseHandle = new CarIllegalCaseHandle();
        carIllegalCaseHandle.setId("12345101");
        CarIllegalCase carIllegalCase = new CarIllegalCase();
        carIllegalCase.setId("123456");
        carIllegalCase.setPayMoney(new BigDecimal(998));
        carIllegalCaseHandle.setIllegalCase(carIllegalCase);
        carIllegalCaseHandleDao.insert(carIllegalCaseHandle);

    }
    @Test
    public void getCarIllegalCaseHandle() {

    }
}