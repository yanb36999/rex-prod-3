package com.zmcsoft.rex.workflow.illegal.controller.my;

import com.zmcsoft.rex.workflow.illegal.api.entity.WechatSendLogger;
import com.zmcsoft.rex.workflow.illegal.api.service.WechatSendLoggerService;
import io.swagger.annotations.Api;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.SimpleGenericEntityController;
import org.hswebframework.web.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouhao
 */
@RestController
@Transactional(rollbackFor = Throwable.class)
@RequestMapping("/wechat/send")
@Authorize(permission = "wechat-send")
@Api(tags = "微信推送日志", value = "")
public class WechatSendLoggerController implements SimpleGenericEntityController<WechatSendLogger, String, QueryParamEntity> {

    private WechatSendLoggerService wechatSendLoggerService;

    @Autowired
    public void setWechatSendLoggerService(WechatSendLoggerService wechatSendLoggerService) {
        this.wechatSendLoggerService = wechatSendLoggerService;
    }

    @Override
    public CrudService<WechatSendLogger, String> getService() {
        return wechatSendLoggerService;
    }


}
