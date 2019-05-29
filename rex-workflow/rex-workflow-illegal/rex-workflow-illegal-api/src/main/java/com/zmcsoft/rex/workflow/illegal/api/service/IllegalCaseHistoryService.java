package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCaseHistory;
import org.hswebframework.web.service.InsertService;
import org.hswebframework.web.service.QueryService;

import java.util.List;

/**
 * @author zhouhao
 * @since 1.0
 */
public interface IllegalCaseHistoryService extends QueryService<IllegalCaseHistory, String>,InsertService<IllegalCaseHistory, String> {

    List<IllegalCaseHistory> selectByCaseId(String dataId);

}
