package com.zmcsoft.rex.tmb.local;

import com.zmcsoft.rex.tmb.illegal.entity.CarInfo;
import com.zmcsoft.rex.tmb.illegal.entity.DriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.DriverLicenseScore;
import com.zmcsoft.rex.tmb.illegal.impl.dao.CarInfoDao;
import com.zmcsoft.rex.tmb.illegal.impl.dao.DriverLicenseDao;
import com.zmcsoft.rex.tmb.illegal.impl.dao.DriverLicenseScoreDao;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyBatisDaoTestApplication.class)
public class DriverLicenseScoreTest {

    @Autowired
    private DriverLicenseDao driverLicenseDao;

    @Autowired
    private DriverLicenseScoreDao driverLicenseScoreDao;
    @Autowired
    private CarInfoDao carInfoDao;

//    @Autowired
//    private DisposeResultDao disposeResultDao;

    @Test
    public void testGet(){
        DriverLicenseScore score = DefaultDSLQueryService.createQuery(driverLicenseScoreDao)
                .where("fileNum", "510100274433")
                .and("name","陈兵")
                .and("driverNumber","510111197108114218")
                .single();
        Assert.assertNotNull(score);
    }

    @Test
    public void testGetCarInfo(){
        CarInfo info = DefaultDSLQueryService.createQuery(carInfoDao)
                .where("idCard", "510102650601318")
                .single();
        Assert.assertNotNull(info);
    }


    @Test
    public void driverLicense(){
        DriverLicense license = DefaultDSLQueryService.createQuery(driverLicenseDao)
                .where("idCard", "510111197108114218")
                .single();

        System.out.println(license.getDriverType());
        Assert.assertNotNull(license);
    }

    @Test
    public void insert(){
//        DisposeResult d = new DisposeResult();
//        d.setBusinessId("122222222222");
//        d.setCaseId("121111111");
//        d.setStatus("已处理");
//        d.setExplainDate(new Date());
//        d.setResult("处理结果");
//        disposeResultDao.insert(d);
    }
}




