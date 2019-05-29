package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.workflow.illegal.impl.dao.IllegalReportLogDao;
import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalReportLog;
import org.hswebframework.web.service.GenericEntityService;
import org.hswebframework.web.id.IDGenerator;
import com.zmcsoft.rex.workflow.illegal.api.service.IllegalReportLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 默认的服务实现
 *
 * @author hsweb-generator-online
 */
@Service("illegalReportLogService")
public class LocalIllegalReportLogService extends GenericEntityService<IllegalReportLog, String>
        implements IllegalReportLogService {
    @Autowired
    private IllegalReportLogDao illegalReportLogDao;

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public IllegalReportLogDao getDao() {
        return illegalReportLogDao;
    }

    @Override
    public List<IllegalReportLog> getByReportId(String reportId) {
        Objects.requireNonNull(reportId,"reportId can not be null");
        return createQuery().where("reportId",reportId).orderByDesc("handleTime").listNoPaging();
    }


}
