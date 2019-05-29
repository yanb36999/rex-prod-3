package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.workflow.illegal.api.entity.CarIllegalCaseHandle;
import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCode;
import com.zmcsoft.rex.workflow.illegal.impl.dao.CarIllegalCaseHandleDao;
import com.zmcsoft.rex.workflow.illegal.impl.dao.IllegalCodeDao;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class IllealCodeDaoTest {

    @Autowired
    private IllegalCodeDao illegalCodeDao;



    @Test
    public void insertIllegalCode() {
        IllegalCode illegalCode = new IllegalCode();
        illegalCode.setId("1111104");
        illegalCodeDao.insert(illegalCode);
    }
    @Test
    public void getCarIllegalCaseHandle() {

    }
}