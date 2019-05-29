package com.zmcsoft.rex.tmb.local;

import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.tmb.illegal.entity.ConfirmInfo;
import com.zmcsoft.rex.tmb.illegal.impl.dao.CarIllegalCaseDao;
import com.zmcsoft.rex.tmb.illegal.impl.service.LocalIllegalCaseService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FtpTest {

    @InjectMocks
    private LocalIllegalCaseService localIllegalCaseService = new LocalIllegalCaseService();

    @Mock
    private CarIllegalCaseDao carIllegalCaseDao = mock(CarIllegalCaseDao.class);

    @Before
    public void init() {
        CarIllegalCase carIllegalCase = new CarIllegalCase();

        when(carIllegalCaseDao.query(any())).thenReturn(Arrays.asList(carIllegalCase));
    }

    @Test
    public void ftpTest() {
//        localIllegalCaseService.setUsername("apsp");
//        localIllegalCaseService.setHost("192.168.1.102");
//        localIllegalCaseService.setPort(21);
//        localIllegalCaseService.setPassword("apsp");
        List<ConfirmInfo> confirmInfos = new ArrayList<>();

        confirmInfos.add(ConfirmInfo.builder().caseId("5101000050001901").confirmDate(new Date()).build());
        confirmInfos.add(ConfirmInfo.builder().caseId("5101000050001904").confirmDate(new Date()).build());
        confirmInfos.add(ConfirmInfo.builder().caseId("5101000050001905").confirmDate(new Date()).build());
        confirmInfos.add(ConfirmInfo.builder().caseId("5101000050001906").confirmDate(new Date()).build());

        boolean confirm = localIllegalCaseService.confirm(confirmInfos);
        Assert.assertTrue(confirm);
    }
}
