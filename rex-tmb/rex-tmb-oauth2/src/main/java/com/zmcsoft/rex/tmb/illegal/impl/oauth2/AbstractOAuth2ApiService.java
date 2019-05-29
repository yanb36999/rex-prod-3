package com.zmcsoft.rex.tmb.illegal.impl.oauth2;

import org.hswebframework.web.authorization.oauth2.client.OAuth2RequestService;
import org.hswebframework.web.authorization.oauth2.client.request.OAuth2Request;
import org.hswebframework.web.authorization.oauth2.client.request.OAuth2Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 */
public abstract class AbstractOAuth2ApiService {

    @Autowired
    protected OAuth2RequestService requestService;

    protected String serviceId = "rex-illegal-api-service";

    protected String apiBasePath = "/";

    protected String apiHost="http://localhost:8089";

    protected Map<String, String> apiList = new HashMap<>();

    private OAuth2Session session;

    public OAuth2Session getSession(){
        if(session==null){
            synchronized (this){
                if(null!=session){
                    return session;
                }
                session=requestService
                        .create(getServiceId())
                        .byClientCredentials();
            }
        }
        return session;
    }

    public String getApi(String name, String... pathVar) {
        return apiHost+apiBasePath + String.format(apiList.get(name), pathVar);
    }

    public void setApiList(Map<String, String> apiList) {
        this.apiList = apiList;
    }

    public OAuth2Request request(String name,String... var){
        return getSession()
                .request(getApi(name, var));
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = apiBasePath;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }

    public void setRequestService(OAuth2RequestService requestService) {
        this.requestService = requestService;
    }

    public String getApiBasePath() {
        return apiBasePath;
    }

    public String getApiHost() {
        return apiHost;
    }

    public String getServiceId() {
        return serviceId;
    }
}
