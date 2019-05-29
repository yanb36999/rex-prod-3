package com.zmcsoft.rex.user.local.dao;

import com.zmcsoft.rex.api.user.entity.UserCar;
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
public class UserCarDaoTest {


    @Autowired
    private UserCarDao userCarDao;

    @Test
    public void testGet(){
        UserCar userCar = DefaultDSLQueryService.createQuery(userCarDao)
                .where("id", "o9IjmjvyWf5Jo1y8DvFUq6L4Te-o")
                .single();
        Assert.assertNotNull(userCar);
    }


    @Test
    public void testUpdate(){
        int id = DefaultDSLUpdateService.createUpdate(userCarDao)
                .where("id", "o9IjmjvyWf5Jo1y8DvFUq6L4Te-o")
                .set("updateTime", new Date())
                .exec();
        Assert.assertTrue(id>0);
    }

    @Test
    public void testDelete(){
        int id = DefaultDSLDeleteService.createDelete(userCarDao)
                .where("id","o9IjmjvyWf5Jo1y8DvFUq6L4Te-o" )
                .exec();
        Assert.assertTrue(id>0);
    }
}
