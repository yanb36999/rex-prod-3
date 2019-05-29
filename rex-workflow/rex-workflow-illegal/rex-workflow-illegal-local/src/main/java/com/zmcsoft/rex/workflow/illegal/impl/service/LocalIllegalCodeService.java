package com.zmcsoft.rex.workflow.illegal.impl.service;


import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCode;
import com.zmcsoft.rex.workflow.illegal.api.service.IllegalCodeService;
import com.zmcsoft.rex.workflow.illegal.impl.dao.IllegalCodeDao;
import org.hswebframework.utils.StringUtils;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.EnableCacheGenericEntityService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author zhouhao
 */
@Transactional(rollbackFor = Throwable.class)
@Service
@CacheConfig(cacheNames = "illegal-code")
public class LocalIllegalCodeService extends EnableCacheGenericEntityService<IllegalCode, String>
        implements IllegalCodeService {

    private IllegalCodeDao illegalCodeDao;

    @Autowired
    public void setIllegalCodeDaoDao(IllegalCodeDao illegalCodeDao) {
        this.illegalCodeDao = illegalCodeDao;
    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return null;
    }

    @Override
    public CrudDao<IllegalCode, String> getDao() {
        return illegalCodeDao;
    }

    @Override
    @Cacheable(key = "'id:'+#code")
    public IllegalCode getByCode(String code) {
        assertNotNull(code);
        return createQuery().where("id", code).single();
    }
}
