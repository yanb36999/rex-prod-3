package com.zmcsoft.rex.api.user.authorization;

import com.zmcsoft.rex.api.user.service.UserService;
import org.hswebframework.web.authorization.basic.web.SessionIdUserTokenParser;
import org.hswebframework.web.authorization.token.UserTokenManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouhao
 * @since
 */
@Configuration
public class RexUserAuthenticationAutoConfiguration {

    @Bean
    public SessionIdUserTokenParser sessionIdUserTokenParser(){
        return new SessionIdUserTokenParser();
    }

    @Bean
    public RexUserTokenParser rexUserTokenParser(UserTokenManager userTokenManager) {
        return new RexUserTokenParser(userTokenManager);
    }

    @Bean
    public RexUserAuthenticationManager rexUserAuthenticationManager(UserService userService) {
        return new RexUserAuthenticationManager(new RexUserAuthenticationInitializeService(userService));
    }

}
