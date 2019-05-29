package com.zmcsoft.rex.workflow.illegal.controller.my;

import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCode;
import com.zmcsoft.rex.workflow.illegal.api.service.IllegalCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.SimpleGenericEntityController;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.hswebframework.web.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouhao
 */
@RestController
@Transactional(rollbackFor = Throwable.class)
@RequestMapping("/illegal/code")
@Authorize(permission = "illegal-code")
@Api(tags = "违法代码信息", value = "illegalCodeApi")
public class IllegalCodeController implements SimpleGenericEntityController<IllegalCode, String, QueryParamEntity> {

    private IllegalCodeService illegalCaseService;

    @Autowired
    public void setIllegalCodeService(IllegalCodeService illegalCaseService) {
        this.illegalCaseService = illegalCaseService;
    }

    @Override
    public CrudService<IllegalCode, String> getService() {
        return illegalCaseService;
    }

    @GetMapping("/by-code/{code}")
    @Authorize(merge = false)
    @ApiOperation("根据违法代码查询详情")
    public ResponseMessage<IllegalCode> getByCode(@ApiParam("违法代码") @PathVariable String code) {
        return ResponseMessage.ok(illegalCaseService.getByCode(code));
    }

}
