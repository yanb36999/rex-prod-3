package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.ConfirmRequest;
import com.zmcsoft.rex.workflow.illegal.api.entity.UserCarIllegalInfo;

import java.util.List;

/**
 * 用户车辆违法信息服务
 *
 * @author zhouhao
 * @since 1.0
 */
public interface UserCarIllegalService {
    /**
     * 根据用户id获取用户车辆全部违法信息
     *
     * @param userId 用户id
     * @return 用户车辆违法信息, 如果没有违法信息, 则:{@link UserCarIllegalInfo#caseDetailList}为空集合,而不是<code>null</code>
     * @see com.zmcsoft.rex.api.user.service.UserServiceManager
     * @see com.zmcsoft.rex.tmb.illegal.service.IllegalCaseService
     */
    UserCarIllegalInfo getByUserId(String userId);

    boolean confirm(String channel,String userId, List<ConfirmRequest> confirmRequest);


}
