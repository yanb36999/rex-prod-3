package com.zmcsoft.rex.starter.logger;

import com.alibaba.fastjson.JSON;
import com.zmcsoft.rex.logging.business.BusinessLogger;
import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCaseHistory;
import com.zmcsoft.rex.logging.business.BusinessLoggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhouhao
 * @since 1.0
 */
@Component
public class Slf4jBusinessLoggerListener implements BusinessLoggerListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onLogger(BusinessLogger illegalCaseHistory) {
        if (logger.isInfoEnabled()) {
            logger.info(JSON.toJSONString(illegalCaseHistory));
        }
    }
}
