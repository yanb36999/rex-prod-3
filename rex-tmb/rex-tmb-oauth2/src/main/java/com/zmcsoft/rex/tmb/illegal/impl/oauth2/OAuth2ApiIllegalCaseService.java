package com.zmcsoft.rex.tmb.illegal.impl.oauth2;

import com.alibaba.fastjson.JSON;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalData;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCaseQueryEntity;
import com.zmcsoft.rex.tmb.illegal.entity.ConfirmInfo;
import com.zmcsoft.rex.tmb.illegal.service.IllegalCaseService;
import org.hswebframework.web.authorization.oauth2.client.OAuth2RequestService;
import org.hswebframework.web.datasource.annotation.UseDefaultDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.sampled.Line;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用交管局的http接口,获取违法案件信息
 *
 * @author zhouhao
 * @since 1.0
 */
@Service
@ConfigurationProperties(prefix = "com.zmcsoft.oauth2")
@UseDefaultDataSource
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.NOT_SUPPORTED)
public class OAuth2ApiIllegalCaseService implements IllegalCaseService {

    private OAuth2RequestService requestService;

    private String serviceId = "rex-illegal-api-service";

    private String apiBasePath = "illegal/case";

    private Map<String, String> apiList = new HashMap<>();

    public OAuth2ApiIllegalCaseService() {
        apiList.put("getByUserCar", "/query");
        apiList.put("getById", "/%s");
        apiList.put("confirm", "/confirm");
        apiList.put("getDataByUserCar", "/summary");
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Autowired
    public void setRequestService(OAuth2RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public List<CarIllegalCase> getByUserCar(CarIllegalCaseQueryEntity car) {
        return requestService.create(serviceId)
                .byClientCredentials()
                .authorize()
                .request(getApi("getByUserCar"))
                .param("plateType", car.getPlateType())
                .param("plateNumber", car.getPlateNumber())
                .param("newIllegal", car.isNewIllegal())
                .get()
                .asList(CarIllegalCase.class);
    }

    @Override
    public CarIllegalCase getById(String caseId) {
        return requestService.create(serviceId)
                .byClientCredentials()
                .authorize()
                .request(getApi("getById", caseId))
                .get().as(CarIllegalCase.class);
    }


    @Override
    public boolean confirm(List<ConfirmInfo> confirmInfos) {
        return requestService.create(serviceId)
                .byClientCredentials()
                .authorize()
                .request(getApi("confirm"))
                .requestBody(JSON.toJSONString(confirmInfos))
                .post()
                .status() == 200;
    }


    @Override
    public CarIllegalData getDataByUserCar(CarIllegalCaseQueryEntity car) {
        return requestService.create(serviceId)
                .byClientCredentials()
                .authorize()
                .request(getApi("getDataByUserCar"))
                .param("plateType", car.getPlateType())
                .param("plateNumber", car.getPlateNumber())
                .get()
                .as(CarIllegalData.class);
    }


    public String getApi(String name, String... pathVar) {
        return apiBasePath + String.format(apiList.get(name), (Object[]) pathVar);
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = apiBasePath;
    }

    public void setApiList(Map<String, String> apiList) {
        this.apiList = apiList;
    }
}
