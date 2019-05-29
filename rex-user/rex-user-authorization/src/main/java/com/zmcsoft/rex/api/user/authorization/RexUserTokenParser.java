package com.zmcsoft.rex.api.user.authorization;

import org.hswebframework.web.authorization.basic.web.AuthorizedToken;
import org.hswebframework.web.authorization.basic.web.ParsedToken;
import org.hswebframework.web.authorization.basic.web.UserTokenParser;
import org.hswebframework.web.authorization.token.UserToken;
import org.hswebframework.web.authorization.token.UserTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhouhao
 * @since
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RexUserTokenParser implements UserTokenParser {

    private UserTokenManager userTokenManager;

    public RexUserTokenParser(UserTokenManager userTokenManager) {
        this.userTokenManager = userTokenManager;
    }

    private CustomRexUserTokenParser customRexUserTokenParser;

    @Autowired(required = false)
    public void setCustomRexUserTokenParser(CustomRexUserTokenParser customRexUserTokenParser) {
        this.customRexUserTokenParser = customRexUserTokenParser;
    }

    @Override
    public ParsedToken parseToken(HttpServletRequest request) {
        String token = request.getHeader("x-access-token");
        if (token == null) {
            return null;
        }
        UserToken userToken = userTokenManager.getByToken(token);
        boolean tokenIsEffective = null == customRexUserTokenParser || (userToken != null && !userToken.isExpired());

        if (tokenIsEffective) {
            return new ParsedToken() {
                @Override
                public String getToken() {
                    return token;
                }

                @Override
                public String getType() {
                    return RexUserAuthenticationManager.TOKEN_TYPE;
                }
            };
        } else {
            //尝试自定义方式获取用户信息
            String userId = customRexUserTokenParser.parseUserId(token);
            if (null == userId) {
                return null;
            }
            return new AuthorizedToken() {
                @Override
                public String getUserId() {
                    return userId;
                }

                @Override
                public String getToken() {
                    return token;
                }

                @Override
                public String getType() {
                    return RexUserAuthenticationManager.TOKEN_TYPE;
                }

                @Override
                public long getMaxInactiveInterval() {
                    return -1;
                }
            };
        }
    }
}
