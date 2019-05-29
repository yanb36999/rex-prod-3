package com.zmcsoft.rex.pay;

import com.zmcsoft.rex.oauth2.server.OAuth2UserTokenParser;
import org.hswebframework.web.authorization.basic.web.SessionIdUserTokenParser;
import org.hswebframework.web.authorization.oauth2.server.token.AccessTokenService;
import org.hswebframework.web.authorization.token.UserTokenManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouhao
 * @since
 */
@Configuration
public class AuthorizationConfiguration {

    @Bean
    public SessionIdUserTokenParser sessionIdUserTokenParser() {
        //继续使用sessionId
        return new SessionIdUserTokenParser();
    }

    @Bean
    public OAuth2UserTokenParser oAuth2UserTokenParser(AccessTokenService tokenService, UserTokenManager tokenManager) {
        //让权限支持OAuth2 token
        return new OAuth2UserTokenParser(tokenService, tokenManager);
    }
}
