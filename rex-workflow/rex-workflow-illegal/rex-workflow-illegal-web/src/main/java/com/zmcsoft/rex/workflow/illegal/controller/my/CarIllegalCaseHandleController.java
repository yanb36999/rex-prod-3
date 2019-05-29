package com.zmcsoft.rex.workflow.illegal.controller.my;

import com.zmcsoft.rex.workflow.illegal.api.entity.CarIllegalCaseHandle;
import com.zmcsoft.rex.workflow.illegal.api.entity.HandleStatusDefine;
import com.zmcsoft.rex.workflow.illegal.api.entity.HandleStatus;
import com.zmcsoft.rex.workflow.illegal.api.entity.UserCarIllegalDetail;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouhao
 */
@RestController
@Transactional(rollbackFor = Throwable.class)
@RequestMapping("/cars/illegal")
@Authorize(permission = "car-illegal")
@Api(tags = "车辆违法案件处理信息", value = "carIllegalCaseApi")
public class  CarIllegalCaseHandleController implements QueryController<CarIllegalCaseHandle, String, QueryParamEntity> {

    private CarIllegalCaseHandleService carIllegalCaseHandleService;

    private DictService dictService;

    private PropertyRefactor propertyRefactor;

    @Autowired
    public void setPropertyRefactor(PropertyRefactor propertyRefactor){
        this.propertyRefactor = propertyRefactor;
    }

    @Autowired
    public void setDictService(DictService dictService){
        this.dictService = dictService;
    }

    @Autowired
    public void setIllegalCodeService(CarIllegalCaseHandleService carIllegalCaseHandleService) {
        this.carIllegalCaseHandleService = carIllegalCaseHandleService;
    }

    @Override
    public CarIllegalCaseHandleService getService() {
        return carIllegalCaseHandleService;
    }

    @GetMapping("/detail/{id}/{userId}/{plateNumber}/{plateType}")
    public ResponseMessage<UserCarIllegalDetail> detail(@PathVariable String id, @PathVariable String userId, @PathVariable String plateNumber, @PathVariable String plateType) {
        UserCarIllegalDetail userCarIllegalDetail = carIllegalCaseHandleService.getUserCarIllegalDetail(id,userId, plateNumber, plateType);
        //重构违法详信息
        userCarIllegalDetail.getCarIllegalCaseHandle().
                setCarInfo(propertyRefactor.applyCarText(userCarIllegalDetail.getCarIllegalCaseHandle().getCarInfo()));
        userCarIllegalDetail.getCarIllegalCaseHandle().
                setDriverLicense(propertyRefactor.applyLicenseText(userCarIllegalDetail.getCarIllegalCaseHandle().getDriverLicense()));
        userCarIllegalDetail.getCarIllegalCaseHandle().
                setIllegalCase(propertyRefactor.applyCaseText(userCarIllegalDetail.getCarIllegalCaseHandle().getIllegalCase()));
        return  ResponseMessage.ok(userCarIllegalDetail);
    }

    @GetMapping("/handle-status")
    @Authorize(merge = false)
    public ResponseMessage<List<HandleStatus>> getHandleStatus(){
        return ResponseMessage.ok(Arrays
                .stream(HandleStatusDefine.values())
                .map(HandleStatusDefine::getStatus)
                .collect(Collectors.toList()));
    }

    @GetMapping("/dict-type/{dictId}/{key}/{defaultValue}")
    public ResponseMessage<String> getDictType(@PathVariable String dictId, @PathVariable String key, @PathVariable String defaultValue){
        return ResponseMessage.ok(dictService.getString(dictId, key ,defaultValue));
    }

    @GetMapping("/dict-all")
    public ResponseMessage<Map<String, Map<String, Object>>> getDictAll(){
        return ResponseMessage.ok(dictService.getAll());
    }
}
