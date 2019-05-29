package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalReportLog;
import org.hswebframework.web.service.CrudService;

import java.util.List;

/**
 *  违法举报日志 服务类
 *
 * @author hsweb-generator-online
 */
public interface IllegalReportLogService extends CrudService<IllegalReportLog, String> {

    List<IllegalReportLog> getByReportId(String reportId);
}
