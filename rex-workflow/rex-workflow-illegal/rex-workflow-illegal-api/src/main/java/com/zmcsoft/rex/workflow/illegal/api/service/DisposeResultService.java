package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.DisposeResult;
import org.hswebframework.web.service.CrudService;

/**
 *  案件处理结果
 */
public interface DisposeResultService extends CrudService<DisposeResult,String>{

    /**
     * 查询处理结果
     * @param caseId
     * @return
     */
    DisposeResult queryResult(String caseId);
}
