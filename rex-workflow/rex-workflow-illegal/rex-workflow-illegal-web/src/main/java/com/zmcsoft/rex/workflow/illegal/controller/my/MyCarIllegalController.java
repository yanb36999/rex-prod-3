package com.zmcsoft.rex.workflow.illegal.controller.my;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.service.UserServiceManager;
import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCaseHistory;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.tmb.illegal.service.IllegalCaseService;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import com.zmcsoft.rex.workflow.illegal.api.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.BusinessException;
import org.hswebframework.web.NotFoundException;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 我的车辆违法信息控制器,用于管理当前用户登录用户的车辆违法信息
 *
 * @author zhouhao
 * @since 1.0
 */
@RestController
@RequestMapping("/my/cars/illegal")
@Api(value = "userCarIllegalApi", tags = "用户车辆违法信息管理")
public class MyCarIllegalController {

    private UserCarIllegalService userCarIllegalService;

    private IllegalCaseService illegalCaseService;

    private UserServiceManager userServiceManager;

    private CarIllegalCaseHandleService handleService;

    private IllegalCaseHistoryService loggerService;

    private DisposeResultService disposeResultService;

    private PropertyRefactor propertyRefactor;

    @Autowired
    public void setPropertyRefactor(PropertyRefactor propertyRefactor) {
        this.propertyRefactor = propertyRefactor;
    }

    @Autowired
    public void setDisposeResultService(DisposeResultService disposeResultService) {
        this.disposeResultService = disposeResultService;
    }

    @Autowired
    public void setLoggerService(IllegalCaseHistoryService loggerService) {
        this.loggerService = loggerService;
    }

    @Autowired
    public void setHandleService(CarIllegalCaseHandleService handleService) {
        this.handleService = handleService;
    }

    @Autowired
    public void setUserServiceManager(UserServiceManager userServiceManager) {
        this.userServiceManager = userServiceManager;
    }

    @Autowired
    public void setIllegalCaseService(IllegalCaseService illegalCaseService) {
        this.illegalCaseService = illegalCaseService;
    }

    @Autowired
    public void setUserCarIllegalService(UserCarIllegalService userCarIllegalService) {
        this.userCarIllegalService = userCarIllegalService;
    }

    @GetMapping
    @ApiOperation("获取车辆违法信息")
    @Authorize
    public ResponseMessage<UserCarIllegalInfo> myCarsIllegalList(Authentication authentication) {

        UserCarIllegalInfo carIllegalInfo = userCarIllegalService.getByUserId(authentication.getUser().getId());

        return ResponseMessage.ok(carIllegalInfo);
    }

    @GetMapping("/{caseId}")
    @ApiOperation("获取指定ID的车辆违法信息")
    @Authorize
    public ResponseMessage<CarIllegalCase> myCarsIllegal(
            @ApiParam("案件ID")
            @PathVariable String caseId
            , Authentication authentication) {
        List<UserCar> cars = userServiceManager.userCarService().getByUserId(authentication.getUser().getId());

        CarIllegalCase carIllegalCase = illegalCaseService.getById(caseId);

        propertyRefactor.applyCaseText(carIllegalCase);

        cars.stream()
                .map(propertyRefactor::applyCarText)
                .filter(car -> car.getPlateType().equals(carIllegalCase.getPlateType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("只能查看自己的违法数据"));

        return ResponseMessage.ok(carIllegalCase);
    }

    @PostMapping("/confirm/{channel}")
    @ApiOperation(value = "提交确认接受处罚")
    @Authorize
    public ResponseMessage<Boolean> confirmMyCarsIllegal(
            @ApiParam(value = "渠道号", required = true)
            @PathVariable String channel,
            @ApiParam(value = "确认参数", required = true)
            @RequestBody List<ConfirmRequest> request,
            Authentication authentication) {

        return ResponseMessage.ok(userCarIllegalService.confirm(channel, authentication.getUser().getId(), request));
    }

    @GetMapping("/confirm/{caseId}/result")
    @ApiOperation(value = "案件确认结果信息")
    @Authorize
    public ResponseMessage<CarIllegalCaseHandle> confirmResult(
            @ApiParam(value = "案件ID", required = true)
            @PathVariable String caseId,
            Authentication authentication) {
        CarIllegalCaseHandle handle = handleService.selectByCaseId(caseId);
        if (handle == null) {
            throw new BusinessException("案件不存在");
        }
        if (!handle.getUserId().equals(authentication.getUser().getId())) {
            throw new BusinessException("只能查看自己的案件");
        }
//        DisposeResult result = disposeResultService.queryResult(businessId);

        return ResponseMessage.ok(handle);
    }

    @PostMapping("/sign/{caseId}")
    @ApiOperation(value = "签收案件信息")
    @Authorize
    public ResponseMessage<Boolean> signIllegal(
            @ApiParam(value = "案件ID", required = true)
            @PathVariable String caseId,
            Authentication authentication) {
        CarIllegalCaseHandle handle = handleService.selectByCaseId(caseId);
        if (handle == null) {
            throw new BusinessException("案件不存在");
        }
        if (!handle.getUserId().equals(authentication.getUser().getId())) {
            throw new BusinessException("只能签收自己的案件");
        }
        return ResponseMessage.ok(handleService.sign(caseId));
    }

    @GetMapping("/penalty-decision/{caseId}")
    @ApiOperation(value = "处罚决定书信息")
    @Authorize
    public ResponseMessage<PenaltyDecision> penaltyDecision(
            @ApiParam(value = "案件ID", required = true)
            @PathVariable String caseId,
            Authentication authentication) {
        PenaltyDecision decision = handleService.selectPenaltyByCaseId(caseId);


        return ResponseMessage.ok(decision);
    }

    @GetMapping("/handles")
    @ApiOperation(value = "已处理处罚信息列表")
    @Authorize
    public ResponseMessage<List<CarIllegalCaseHandle>> handles(Authentication authentication) {
        List<CarIllegalCaseHandle> handleList = handleService.selectByUserId(authentication.getUser().getId());

        return ResponseMessage.ok(handleList.stream()
                .peek(handle -> propertyRefactor.applyCaseText(handle.getIllegalCase()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/handles/{caseId}")
    @ApiOperation(value = "已处理处罚信息详情")
    @Authorize
    public ResponseMessage<CarIllegalCaseHandle> penaltiesDetail(@ApiParam("案件ID")
                                                                 @PathVariable String caseId,
                                                                 Authentication authentication) {
        CarIllegalCaseHandle handle = handleService.selectByCaseId(caseId);
        if (!authentication.getUser().getId().equals(handle.getUserId())) {
            return ResponseMessage.ok();
        }
        propertyRefactor.applyCaseText(handle.getIllegalCase());
        return ResponseMessage.ok(handle);
    }

    @GetMapping("/history/{caseId}")
    @Authorize
    @ApiOperation("获取案件历史")
    public ResponseMessage<List<IllegalCaseHistory>> getMyCarLogger(@ApiParam("案件ID")
                                                                @PathVariable String caseId,
                                                                    Authentication authentication) {
        CarIllegalCaseHandle handle = handleService.selectByCaseId(caseId);
        if (handle == null || !authentication.getUser().getId().equals(handle.getUserId())) {
            return ResponseMessage.ok(Collections.emptyList());
        }
        return ResponseMessage.ok(loggerService.selectByCaseId(handle.getId()));
    }


    @GetMapping("/afresh-commit/{caseId}")
    @Authorize
    @ApiOperation("重新提交")
    public ResponseMessage<Boolean> reCommit(@ApiParam(value = "案件ID", required = true)
                                    @PathVariable String caseId){
        return ResponseMessage.ok(handleService.afreshCommit(caseId));
    }

}
