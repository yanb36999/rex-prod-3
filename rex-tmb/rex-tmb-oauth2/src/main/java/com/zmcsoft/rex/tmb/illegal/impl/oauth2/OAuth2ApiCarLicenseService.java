package com.zmcsoft.rex.tmb.illegal.impl.oauth2;

import com.zmcsoft.rex.tmb.illegal.entity.*;
import com.zmcsoft.rex.tmb.illegal.service.DriverLicenseService;
import org.hswebframework.web.authorization.oauth2.client.OAuth2RequestService;
import org.hswebframework.web.datasource.annotation.UseDefaultDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhouhao
 * @since
 */
@Service
@ConfigurationProperties(prefix = "com.zmcsoft.oauth2")
@UseDefaultDataSource
@Transactional(rollbackFor = Throwable.class,propagation = Propagation.NOT_SUPPORTED)
public class OAuth2ApiCarLicenseService implements DriverLicenseService {

    private OAuth2RequestService requestService;

    private String serviceId = "rex-illegal-api-service";

    private String apiBasePath = "driver-license";

    private Map<String, String> apiList = new HashMap<>();

    public OAuth2ApiCarLicenseService() {
        apiList.put("getScore", "/score");
        apiList.put("driverDetail", "/%s");
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    @Autowired
    public void setRequestService(OAuth2RequestService requestService) {
        this.requestService = requestService;
    }

    public String getApi(String name, String... pathVar) {
        return apiBasePath + String.format(apiList.get(name), pathVar);
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = apiBasePath;
    }

    public void setApiList(Map<String, String> apiList) {
        this.apiList = apiList;
    }

    @Override
    public Integer getScore(DriverLicenseScore driverLicenseScore) {
        return requestService.create(serviceId)
                .byClientCredentials()
                .authorize()
                .request(getApi("getScore"))
                .param("name", driverLicenseScore.getName())
                .param("driverNumber", driverLicenseScore.getDriverNumber())
                .param("fileNumber", driverLicenseScore.getFileNumber())
                .get().as(Integer.class);
    }

    @Override
    public DriverLicense driverDetail(String idCard) {
        return requestService.create(serviceId)
                .byClientCredentials()
                .authorize()
                .request(getApi("driverDetail", idCard))
                .get().as(DriverLicense.class);
    }
}
