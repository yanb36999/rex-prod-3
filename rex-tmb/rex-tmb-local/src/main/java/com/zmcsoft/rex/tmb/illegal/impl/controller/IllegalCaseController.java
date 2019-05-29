package com.zmcsoft.rex.tmb.illegal.impl.controller;

import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCaseQueryEntity;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalData;
import com.zmcsoft.rex.tmb.illegal.entity.ConfirmInfo;
import com.zmcsoft.rex.tmb.illegal.impl.service.LocalIllegalCaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hswebframework.web.authorization.Permission;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouhao
 */
@RestController
@Transactional(rollbackFor = Throwable.class)
@RequestMapping("/illegal/case")
@Authorize(permission = "illegal-case")
@Api(tags = "违法信息")
public class IllegalCaseController {

    private LocalIllegalCaseService illegalCaseService;

    @Autowired
    public void setIllegalCaseService(LocalIllegalCaseService illegalCaseService) {
        this.illegalCaseService = illegalCaseService;
    }

    @GetMapping("/query")
    @Authorize(action = Permission.ACTION_GET)
    @ApiOperation("根据号牌种类获取违法案件信息")
    public List<CarIllegalCase> getByUserCar(CarIllegalCaseQueryEntity car) {
        return illegalCaseService.getByUserCar(car);
    }

    @GetMapping("/{caseId}")
    @Authorize(action = Permission.ACTION_GET)
    @ApiOperation("根据案件id获取违法案件信息")
    public CarIllegalCase getById(@PathVariable String caseId) {
        return illegalCaseService.getById(caseId);
    }

    @PostMapping("/confirm")
    @Authorize(action = "confirm")
    public boolean confirm(@ApiParam(value = "确认参数")
                           @RequestBody List<ConfirmInfo> confirmInfo) {
        return illegalCaseService.confirm(confirmInfo);
    }

    @GetMapping("/summary")
    @Authorize(action = Permission.ACTION_GET)
    @ApiOperation("获取案件统计信息")
    public CarIllegalData getDataByUserCar(@Validated CarIllegalCaseQueryEntity car) {
        return illegalCaseService.getDataByUserCar(car);
    }
}
