package com.zmcsoft.rex.tmb.illegal.impl.dao;

import com.zmcsoft.rex.tmb.illegal.entity.DriverLicense;
import org.hswebframework.web.dao.Dao;
import org.hswebframework.web.dao.dynamic.QueryByEntityDao;

public interface DriverLicenseDao extends QueryByEntityDao<DriverLicense>,Dao {
    DriverLicense selectByIdCard(String id);
}
