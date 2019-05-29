package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.workflow.illegal.api.entity.WechatSendLogger;
import com.zmcsoft.rex.workflow.illegal.api.service.WechatSendLoggerService;
import com.zmcsoft.rex.workflow.illegal.impl.dao.WechatSendLoggerDao;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.dao.CrudDao;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.CrudService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 微信推送消息日志
 *
 * @author zhouhao
 * @since 1.0
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Service
@UseDataSource("loggerDb")
public class LocalWechatSendLoggerService extends GenericEntityService<WechatSendLogger, String> implements WechatSendLoggerService {

    private WechatSendLoggerDao wechatSendLoggerDao;

    @Autowired
    public void setWechatSendLoggerDao(WechatSendLoggerDao wechatSendLoggerDao) {
        this.wechatSendLoggerDao = wechatSendLoggerDao;
    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return IDGenerator.MD5;
    }

    @Override
    public CrudDao<WechatSendLogger, String> getDao() {
        return wechatSendLoggerDao;
    }

    @Override
    public String insert(WechatSendLogger entity) {
        entity.setCreateTime(new Date());
        return super.insert(entity);
    }

    @Override
    public List<WechatSendLogger> queryByTime(List<String>ids,Long startTime, Long endTime) {

        List<WechatSendLogger> wechatSendLoggers = selectByPk(ids);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = new Date(new Long(startTime));
            Date endDate = new Date(new Long(endTime));
            String start= simpleDateFormat.format(startDate);
            String end= simpleDateFormat.format(endDate);
        return createQuery().sql("create_time BETWEEN ? and ?", start, end).listNoPaging();
    }


}
