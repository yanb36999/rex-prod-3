package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalReportLog;
import com.zmcsoft.rex.workflow.illegal.api.entity.ReportIllegalDetail;
import org.hswebframework.web.commons.entity.Entity;
import org.hswebframework.web.commons.entity.PagerResult;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;

import java.util.List;

public interface ReportIllegalService {

    /**
     * 分页，动态查询
     *
     * @param entity
     * @return
     */
    PagerResult<ReportIllegalDetail> getNewReport(List<String> orgCode, QueryParamEntity entity);

    PagerResult<ReportIllegalDetail> getReport(List<String> orgCode, QueryParamEntity entity);

    ReportIllegalDetail getReportDetail(String id, String plateType, String plateNumber);

    int updateNewReportStatus(String reportId, String plateType, String plateNumber, String status);

    int updateCheckFailStatus(String reportId, String plateType, String plateNumber, String status);

    List<IllegalReportLog> getCheckBeforeLogs(String reportId);

    ReportIllegalDetail getNewReportDetail(String id, String plateType, String plateNumber);

    void checkReport(String id, ReportIllegalDetail detail);


    List<ReportIllegalDetail> getOld(String plateNo,String plateType);


    boolean updateOld(String report,String plateNo,String plateType);
}
