package com.zmcsoft.rex.workflow.illegal.impl.message;

import com.alibaba.fastjson.JSON;
import com.zmcsoft.rex.message.DefaultMessageSenders;
import com.zmcsoft.rex.workflow.illegal.api.service.WechatSendLoggerService;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * TODO 完成注释
 *
 * @author zhouhao
 * @since
 */
@RunWith(MockitoJUnitRunner.class)
public class IllegalWechatMessageSenderProviderTest {

    @Mock
    private WechatSendLoggerService wechatSendLoggerService;

    @Mock
    private Environment environment;


    @InjectMocks
    private IllegalWechatMessageSenderProvider senderProvider = new IllegalWechatMessageSenderProvider();

    private CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void init() {
        when(wechatSendLoggerService.insert(any())).then(invocationOnMock -> {
            System.out.println("添加日志" + JSON.toJSONString(invocationOnMock.getArguments()[0]));
            latch.countDown();
            return "1234";
        });
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        senderProvider.init();
    }


    @Test
    public void testSendMsg() throws InterruptedException {
        DefaultMessageSenders senders = new DefaultMessageSenders();
        senders.register(senderProvider);


        senders.wechat("illegal")
                .template(IllegalMessageSendTemplate.builder()
                        .illeaglTime("2017-12-06")
                        .illegalAddress("四川某某大道")
                        .illegalBehavior("闯红灯1")
                        .illegalDecision("123456789")
                        .punishResult("200元/3分")
                        .build().getParamMap())
                .title("申请成功")
                .to("o9IjmjruM8MJV8MTm95qWtdHSSSc")
                .send();
        latch.await(1, TimeUnit.MINUTES);
    }

}