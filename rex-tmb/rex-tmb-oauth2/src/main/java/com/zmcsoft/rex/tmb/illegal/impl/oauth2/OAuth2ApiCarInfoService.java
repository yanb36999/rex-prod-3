package com.zmcsoft.rex.tmb.illegal.impl.oauth2;

import com.zmcsoft.rex.tmb.illegal.entity.CarInfo;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoDetailRequest;
import com.zmcsoft.rex.tmb.illegal.entity.CarInfoRequest;
import com.zmcsoft.rex.tmb.illegal.service.CarInfoService;
import org.hswebframework.web.authorization.oauth2.client.OAuth2RequestService;
import org.hswebframework.web.datasource.annotation.UseDefaultDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用交管局的http接口,获取车辆信息
 *
 * @author zhouhao
 * @since 1.0
 */
@Service
@ConfigurationProperties(prefix = "com.zmcsoft.oauth2")
@UseDefaultDataSource
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.NOT_SUPPORTED)
public class OAuth2ApiCarInfoService implements CarInfoService {

    private OAuth2RequestService requestService;

    private String serviceId = "rex-illegal-api-service";

    private String apiBasePath = "car";

    private Map<String, String> apiList = new HashMap<>();

    public OAuth2ApiCarInfoService() {
        apiList.put("getCarInfo", "/info");
        apiList.put("carInfoDetail", "/detail");

    }

    @Autowired
    public void setRequestService(OAuth2RequestService requestService) {
        this.requestService = requestService;
    }

    public String getApi(String name, String... pathVar) {
        return apiBasePath + String.format(apiList.get(name), (Object[]) pathVar);
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = apiBasePath;
    }

    public void setApiList(Map<String, String> apiList) {
        this.apiList = apiList;
    }

    @Override
    public List<CarInfo> getCarInfo(CarInfoRequest carInfoRequest) {
        return requestService.create(serviceId)
                .byClientCredentials()
                .authorize()
                .request(getApi("getCarInfo"))
                .param("owner", carInfoRequest.getOwner())
                .param("idCard", carInfoRequest.getIdCard())
                .get().asList(CarInfo.class);
    }

    @Override
    public CarInfo carInfoDetail(CarInfoDetailRequest carInfoDetailRequest) {
        return requestService.create(serviceId)
                .byClientCredentials()
                .authorize()
                .request(getApi("carInfoDetail"))
                .param("plateType", carInfoDetailRequest.getPlateType())
                .param("plateNumber", carInfoDetailRequest.getPlateNumber())
                .get().as(CarInfo.class);
    }
}
