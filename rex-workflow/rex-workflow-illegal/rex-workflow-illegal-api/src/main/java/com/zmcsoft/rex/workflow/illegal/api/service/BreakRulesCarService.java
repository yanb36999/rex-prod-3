package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.BreakRulesCar;

import java.util.List;

/**
 * 违法处罚车辆
 */
public interface BreakRulesCarService {

    /**
     * 根据用户ID查询违法处罚车辆
     * @param userId
     * @return
     */
    List<BreakRulesCar> getByUserId(String userId);
}
