package com.zmcsoft.rex.tmb.illegal.impl.dao;

import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import org.hswebframework.web.dao.Dao;
import org.hswebframework.web.dao.dynamic.QueryByEntityDao;
import org.hswebframework.web.dao.dynamic.UpdateByEntityDao;

import java.util.List;
import java.util.Map;

/**
 * @author zhouhao
 */
public interface CarIllegalCaseDao extends QueryByEntityDao<CarIllegalCase>, UpdateByEntityDao, Dao {
    List<CarIllegalCase> selectByMap(Map<String, Object> param);
}
