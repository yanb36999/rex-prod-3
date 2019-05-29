package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.WechatSendLogger;
import org.hswebframework.web.service.CrudService;

import java.util.List;


/**
 * 微信推送消息日志
 *
 * @author zhouhao
 * @since 1.0
 */
public interface WechatSendLoggerService extends CrudService<WechatSendLogger, String> {

    List<WechatSendLogger> queryByTime(List<String>ids,Long startTime,Long endTime);
}
