package com.zmcsoft.rex.user.local.dao;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import org.hswebframework.web.service.DefaultDSLDeleteService;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.hswebframework.web.service.DefaultDSLUpdateService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisDaoTestApplication.class)
public class UserDriverLicenseTest {

    @Autowired
    private UserDriverLicenseDao userDriverLicenseDao;

    @Test
    public void testGet(){
        UserDriverLicense userDriverLicense = DefaultDSLQueryService.createQuery(userDriverLicenseDao)
                .where("id", "o9IjmjvyWf5Jo1y8DvFUq6L4Te-o")
                .single();
        Assert.assertNotNull(userDriverLicense);
    }


    @Test
    public void testUpdate(){
        int id = DefaultDSLUpdateService.createUpdate(userDriverLicenseDao)
                .where("id", "o9IjmjvyWf5Jo1y8DvFUq6L4Te-o")
                .set("createTime", new Date())
                .exec();
        Assert.assertTrue(id>0);
    }

    @Test
    public void testDelete(){
        int id = DefaultDSLDeleteService.createDelete(userDriverLicenseDao)
                .where("id","o9IjmjvyWf5Jo1y8DvFUq6L4Te-o" )
                .exec();
        Assert.assertTrue(id>0);
    }
}
