package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCode;
import org.hswebframework.web.service.CrudService;

/**
 * 违法代码信息服务
 *
 * @author zhouhao
 * @since 1.0
 */
public interface IllegalCodeService extends CrudService<IllegalCode, String> {
    IllegalCode getByCode(String code);
}
