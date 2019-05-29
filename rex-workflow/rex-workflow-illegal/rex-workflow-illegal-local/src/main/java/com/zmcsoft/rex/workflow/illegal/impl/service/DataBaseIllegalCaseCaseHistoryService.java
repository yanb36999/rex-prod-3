package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCaseHistory;
import com.zmcsoft.rex.workflow.illegal.api.service.IllegalCaseHistoryService;
import com.zmcsoft.rex.workflow.illegal.impl.dao.IllegalHistoryDao;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author zhouhao
 * @since
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DataBaseIllegalCaseCaseHistoryService extends GenericEntityService<IllegalCaseHistory, String>
        implements IllegalCaseHistoryService {

    @Autowired
    private IllegalHistoryDao illegalHistoryDao;

    @Override
    public List<IllegalCaseHistory> selectByCaseId(String caseId) {
        Objects.requireNonNull(caseId);
        return createQuery()
                //.where("key", Constants.LOGGER_KEY)
                .and("caseId", caseId)
                .orderByDesc("createTime")
                .listNoPaging();
    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<IllegalCaseHistory, String> getDao() {
        return illegalHistoryDao;
    }


}
