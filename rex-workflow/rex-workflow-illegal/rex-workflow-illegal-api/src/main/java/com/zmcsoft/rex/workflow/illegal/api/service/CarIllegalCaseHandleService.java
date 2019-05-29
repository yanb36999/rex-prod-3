package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.*;
import org.hswebframework.web.service.CrudService;

import java.util.List;
import java.util.Map;

/**
 * 车辆违法案件服务
 *
 * @author zhouhao
 * @since 1.0
 */
public interface CarIllegalCaseHandleService extends CrudService<CarIllegalCaseHandle, String> {

    /**
     * 根据userId获取案件处理信息
     *
     * @param userId       用户id
     * @param handleStatus 获取指定状态的数据,多个状态 关系为 or
     * @return 案件信息, 不存在返回空集合而不是null
     */
    List<CarIllegalCaseHandle> selectByUserId(String userId, Byte... handleStatus);

    CarIllegalCaseHandle selectByCaseId(String caseId);

    UserCarIllegalDetail getUserCarIllegalDetail(String id,String userId, String plateNumber, String plateType);

    boolean sign(String caseId);

    PenaltyDecision selectPenaltyByCaseId(String caseId);

    boolean updatePayStatus(String decisionNumber,String paySign,Byte handleStatus);

    List<CarIllegalCaseHandle> getByHandleStatus(Byte handleStatus);

    List<CarIllegalCaseHandle> getByIllegalXhs(List<String> xhs);

    CarIllegalCaseHandle getLicenseScore(String userId);


    boolean afreshCommit(String caseId);

    CarIllegalCaseHandle getByDecisionNo(String decisionNo);
}
