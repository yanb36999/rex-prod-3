package com.zmcsoft.rex.workflow.illegal.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import com.zmcsoft.rex.workflow.illegal.api.service.CarIllegalCaseHandleService;
import com.zmcsoft.rex.workflow.illegal.api.service.DictService;
import com.zmcsoft.rex.workflow.illegal.api.service.PropertyRefactor;
import io.swagger.annotations.Api;
import org.hswebframework.web.authorization.annotation.Authorize;
import org.hswebframework.web.commons.entity.param.QueryParamEntity;
import org.hswebframework.web.controller.QueryController;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhouhao
 */
@RestController
@Transactional(rollbackFor = Throwable.class)
@RequestMapping("/api/cars/illegal")
@Authorize(ignore = true)
@Api(tags = "车辆违法案件处理信息接口", value = "carIllegalCaseApi")
public class ApiCarIllegalCaseController {

    private CarIllegalCaseHandleService carIllegalCaseHandleService;


    @Autowired
    public void setIllegalCodeService(CarIllegalCaseHandleService carIllegalCaseHandleService) {
        this.carIllegalCaseHandleService = carIllegalCaseHandleService;
    }


    /**
     * 根据handleStatus获取违法案例信息
     * @param handleStatus
     * @return
     */
    @GetMapping("/{handleStatus}")
    public ResponseMessage<List<CarIllegalCaseHandle>> illegalList(@PathVariable Byte handleStatus) {
        return  ResponseMessage.ok(carIllegalCaseHandleService.getByHandleStatus(handleStatus));
    }

    @GetMapping("/single/{id}")
    public ResponseMessage<JSONObject> carIllegalCaseHandleById(@PathVariable String id){
        CarIllegalCaseHandle carIllegalCaseHandle = carIllegalCaseHandleService.selectByPk(id);
        JSONObject object = new JSONObject();
        object.put("paySign",carIllegalCaseHandle.getIllegalCase().getPaySign());
        object.put("handleStatus",carIllegalCaseHandle.getHandleStatus());
        object.put("decisionNumber",carIllegalCaseHandle.getDecisionNumber());
        object.put("plateNumber",carIllegalCaseHandle.getCarInfo().getPlateNumber());
        object.put("driverName",carIllegalCaseHandle.getDriverLicense().getDriverName());
        object.put("licenseNumber",carIllegalCaseHandle.getDriverLicense().getLicenseNumber());
        return  ResponseMessage.ok(object );
    }

    @PostMapping("/xhs")
    public ResponseMessage<List<Map<String,Object>>> testList(String[] xhs){
        List<CarIllegalCaseHandle> carIllegalCaseHandles = carIllegalCaseHandleService.getByIllegalXhs(Arrays.asList(xhs));
        List<Map<String,Object>> result = new ArrayList<>();
        carIllegalCaseHandles.forEach(line->{
            Map<String,Object> tempMap = new HashMap<>();
            tempMap.put("paySign",line.getIllegalCase().getPaySign());
            tempMap.put("handleStatus",line.getHandleStatus());
            tempMap.put("decisionNumber",line.getDecisionNumber());
            tempMap.put("plateNumber",line.getCarInfo().getPlateNumber());
            tempMap.put("driverName",line.getDriverLicense().getDriverName());
            tempMap.put("licenseNumber",line.getDriverLicense().getLicenseNumber());
            tempMap.put("xh",line.getIllegalCase().getId());
            result.add(tempMap);
        });
        return  ResponseMessage.ok(result);
    }
}
