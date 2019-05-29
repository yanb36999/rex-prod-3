package com.zmcsoft.rex.user.local.dao;

import com.zmcsoft.rex.api.user.entity.User;
import org.hswebframework.web.service.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MybatisDaoTestApplication.class)
public class UserDaoTest {

    @Autowired
    private RexUserDao userDao;

    @Test
    public void testGet() {
        User user = DefaultDSLQueryService.createQuery(userDao)
                .where("id", "o9IjmjvyWf5Jo1y8DvFUq6L4Te-o")
                .single();
        Assert.assertNotNull(user);

    }

    @Test
    public void testUpdate(){
        int id = DefaultDSLUpdateService.createUpdate(userDao)
                .where("id", "o9IjmjvyWf5Jo1y8DvFUq6L4Te-o")
                .set("idCard", "123123123132")
                .exec();
        Assert.assertTrue(id>0);
    }

    @Test
    public void testDelete(){
        int id = DefaultDSLDeleteService.createDelete(userDao)
                .where("id","o9IjmjvyWf5Jo1y8DvFUq6L4Te-o" )
                .exec();
        Assert.assertTrue(id>0);
    }

}
