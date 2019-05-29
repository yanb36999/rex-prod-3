package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.ReportInfo;
import org.hswebframework.web.service.CrudService;

import java.util.List;

/**
 * 用户违法举报
 */
public interface ReportInfoService extends CrudService<ReportInfo,String> {


    List<ReportInfo> getByStatus();


    ReportInfo getByOpenId(String openId);

    ReportInfo getByWaterId(String waterId);

    ReportInfo getByWaterIdAndStatus(String waterId,String status);

}
