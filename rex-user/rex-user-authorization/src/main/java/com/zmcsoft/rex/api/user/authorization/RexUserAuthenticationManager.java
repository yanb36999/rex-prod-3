package com.zmcsoft.rex.api.user.authorization;

import com.zmcsoft.rex.api.user.service.UserService;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.AuthenticationInitializeService;
import org.hswebframework.web.authorization.token.ThirdPartAuthenticationManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author zhouhao
 * @since
 */
public class RexUserAuthenticationManager implements ThirdPartAuthenticationManager {

    private AuthenticationInitializeService authenticationInitializeService;

    public static final String TOKEN_TYPE = "rex-user";

    @Override
    public String getTokenType() {
        return TOKEN_TYPE;
    }

    public RexUserAuthenticationManager(AuthenticationInitializeService authenticationInitializeService) {
        this.authenticationInitializeService = authenticationInitializeService;
    }

    @Override
    @Cacheable(value = UserService.USER_AUTH_CACHE_NAME, key = "#userId")
    public Authentication getByUserId(String userId) {
        return authenticationInitializeService.initUserAuthorization(userId);
    }

}
