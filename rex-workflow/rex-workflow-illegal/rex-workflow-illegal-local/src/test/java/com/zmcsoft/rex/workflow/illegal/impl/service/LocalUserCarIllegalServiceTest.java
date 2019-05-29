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
import static org.junit.Assert.*;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 * @since
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalUserCarIllegalServiceTest {

    @Mock
    private UserService userService = mock(UserService.class);

    @Mock
    private UserCarService userCarService = mock(UserCarService.class);

    @Mock
    private UserDriverLicenseService userDriverLicenseService = mock(UserDriverLicenseService.class);

    @Mock
    private UserServiceManager userServiceManager = mock(UserServiceManager.class);

    @Mock
    private IllegalCaseService illegalCaseService;

    @InjectMocks
    private LocalUserCarIllegalService illegalService;

    @Before
    public void init() {
        User user = User.builder().idCard("1234").name("张三").build();
        user.setId("1");
        UserCar userCar = UserCar.builder().userId("1").build();
        UserDriverLicense userDriverLicense = UserDriverLicense.builder().userId("1").build();
        List<UserCar> userCars = new ArrayList<>();
        userCars.add(userCar);
        List<CarIllegalCase> carIllegalCases = new ArrayList<>();
        CarIllegalCase carIllegalCase = new CarIllegalCase();
        carIllegalCase.setId("illgal-id");
        carIllegalCase.setIllegalActivities("闯红灯");
        carIllegalCases.add(carIllegalCase);

        when(illegalCaseService.getByUserCar(any())).thenReturn(carIllegalCases);
        when(userServiceManager.userCarService()).thenReturn(userCarService);
        when(userServiceManager.userDriverLicenseService()).thenReturn(userDriverLicenseService);
        when(userServiceManager.userService()).thenReturn(userService);
        when(userServiceManager.userCarService().getByUserId(user.getId())).thenReturn(userCars);
        when(userServiceManager.userDriverLicenseService().getByUserId(user.getId())).thenReturn(userDriverLicense);
        when(userServiceManager.userService().getById(user.getId())).thenReturn(user);
    }

    @Test
    public void testGetUserCarIllegal() {
        UserCarIllegalInfo userCarIllegalInfo = illegalService.getByUserId("1");
        Assert.assertEquals(userCarIllegalInfo.getUser().getName(), "张三");
        Assert.assertNotNull(userCarIllegalInfo.getCaseDetailList());
        Assert.assertEquals(userCarIllegalInfo.getCaseDetailList().get(0).getIllegalCaseList().get(0).getIllegalActivities(), "闯红灯");
    }
}