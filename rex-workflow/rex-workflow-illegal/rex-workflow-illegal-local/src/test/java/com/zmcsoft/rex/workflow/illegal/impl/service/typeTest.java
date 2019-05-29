package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.api.user.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 * @since
 */
@RunWith(MockitoJUnitRunner.class)
public class typeTest {


    @InjectMocks
    private SimpleDictService simpleDictService;

    @Before
    public void init() {
        //when(userServiceManager.userService().getById(user.getId())).thenReturn(user);
    }

    @Test
    public void testGetUserCarIllegal() {

        String  s = simpleDictService.getString("car-status","A2","2");
    }
}