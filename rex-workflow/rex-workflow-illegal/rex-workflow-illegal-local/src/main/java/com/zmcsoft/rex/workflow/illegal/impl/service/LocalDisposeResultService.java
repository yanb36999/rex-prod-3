package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.DisposeResult;
import com.zmcsoft.rex.workflow.illegal.api.service.DisposeResultService;
import com.zmcsoft.rex.workflow.illegal.impl.dao.DisposeResultDao;
import org.hswebframework.expands.request.RequestBuilder;
import org.hswebframework.expands.request.SimpleRequestBuilder;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.DefaultDSLQueryService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


//@Transactional(rollbackFor = Throwable.class)
@Service
@ConfigurationProperties(prefix = "com.zmcsoft.tmb.ftp")
@Transactional(rollbackFor = Throwable.class)
public class LocalDisposeResultService extends GenericEntityService<DisposeResult,String> implements DisposeResultService {

    private RequestBuilder requestBuilder = new SimpleRequestBuilder();

    private String host;

    private int port;

    private String username;

    private String password;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private DisposeResultDao disposeResultDao;

    @Autowired
    public void setDisposeResultDao(DisposeResultDao disposeResultDao) {
        this.disposeResultDao = disposeResultDao;
    }

    /**
     * 根据案件ID查询处理详情
     * @param caseId
     * @return
     */
    @Override
    public DisposeResult queryResult(String caseId) {
        return DefaultDSLQueryService.createQuery(disposeResultDao)
                .selectExcludes("fileContent")//不查询文件内容
                .where("caseId", caseId)
                .single();
    }


    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<DisposeResult, String> getDao() {
        return disposeResultDao;
    }
}
