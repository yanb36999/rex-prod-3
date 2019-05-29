package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.api.user.entity.User;
import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.api.user.service.UserCarService;
import com.zmcsoft.rex.api.user.service.UserDriverLicenseService;
import com.zmcsoft.rex.api.user.service.UserService;
import com.zmcsoft.rex.api.user.service.UserServiceManager;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.tmb.illegal.service.IllegalCaseService;
import com.zmcsoft.rex.workflow.illegal.api.entity.UserCarIllegalInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 * @since
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalCarIllegalCaseHandleServiceTest {

    @Mock
    private UserService userService = mock(UserService.class);

    @InjectMocks
    private LocalCarIllegalCaseHandleService localCarIllegalCaseHandleService;

    @Before
    public void init() {
        //when(userServiceManager.userService().getById(user.getId())).thenReturn(user);
    }

    @Test
    public void testGetUserCarIllegal() {
     //  boolean resutl = localCarIllegalCaseHandleService.sendMessageToWechat("o9IjmjlW7DRWrd8-Ja7twmBoQ96g","你好。","问候语。","你好");
        //Assert.assertTrue(resutl);
    }
}