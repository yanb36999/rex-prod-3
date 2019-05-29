package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.ReportIllegalCar;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReportInfo;
import com.zmcsoft.rex.workflow.illegal.api.service.ReportIllegalCarService;
import com.zmcsoft.rex.workflow.illegal.api.service.ReportInfoService;
import com.zmcsoft.rex.workflow.illegal.impl.dao.ReportInfoDao;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Throwable.class)
public class LocalReportInfoService extends GenericEntityService<ReportInfo, String> implements ReportInfoService {


    @Autowired
    private ReportInfoDao reportInfoDao;

    @Override
    public List<ReportInfo> getByStatus() {
        return null;
    }

    @Override
    public ReportInfo getByOpenId(String openId) {
        return DefaultDSLQueryService.createQuery(reportInfoDao)
                .where("openId",openId)
                .single();
    }

    @Override
    public ReportInfo getByWaterId(String waterId) {
        return DefaultDSLQueryService.createQuery(reportInfoDao)
                .where("reportId",waterId)
                .single();
    }


    @Override
    public ReportInfo getByWaterIdAndStatus(String waterId,String status) {
        return DefaultDSLQueryService.createQuery(reportInfoDao)
                .where("reportId",waterId)
                .and("dspStatus",status)
                .orderByDesc("reportDate")
                .single();
    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<ReportInfo, String> getDao() {
        return reportInfoDao;
    }
}
