package com.zmcsoft.rex.workflow.illegal.controller;

import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.QueryController;
import org.hswebframework.web.controller.SimpleGenericEntityController;
import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalReportLog;
import org.hswebframework.web.logging.AccessLogger;
import  com.zmcsoft.rex.workflow.illegal.api.service.IllegalReportLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  违法举报日志
 *
 * @author hsweb-generator-online
 */
@RestController
@RequestMapping("/illegal/report/logs")
@Authorize(permission = "illegal-report")
@AccessLogger("违法举报日志")
public class IllegalReportLogController implements QueryController<IllegalReportLog, String, QueryParamEntity> {

    private IllegalReportLogService illegalReportLogService;
  
    @Autowired
    public void setIllegalReportLogService(IllegalReportLogService illegalReportLogService) {
        this.illegalReportLogService = illegalReportLogService;
    }
  
    @Override
    public IllegalReportLogService getService() {
        return illegalReportLogService;
    }
}
