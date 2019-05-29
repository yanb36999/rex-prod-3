package com.zmcsoft.rex.tmb.illegal.impl.dao;

import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfo;
import org.hswebframework.web.dao.Dao;
import org.hswebframework.web.dao.dynamic.QueryByEntityDao;

import java.util.List;
import java.util.Map;

public interface CarInfoDao extends QueryByEntityDao<CarInfo>,Dao {

    List<CarInfo> selectListByMap(Map<String, Object> param);
}
