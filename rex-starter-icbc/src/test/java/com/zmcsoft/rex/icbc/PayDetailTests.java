package com.zmcsoft.rex.icbc;

import com.zmcsoft.rex.entity.PayDetail;
import com.zmcsoft.rex.service.PayDetailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {ICBCServerApplication.class}
)
@ActiveProfiles("prod")
public class PayDetailTests extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    PayDetailService payDetailService;

    @Test
    public void testQuery() throws Exception {
        List<PayDetail> details= payDetailService.selectByBookDate("2017-11-14");
        System.out.println(details);
    }
}
