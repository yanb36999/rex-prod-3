package com.zmcsoft.rex.api.user.authorization;

import com.zmcsoft.rex.api.user.authorization.builder.PermissionBuilder;
import com.zmcsoft.rex.api.user.authorization.builder.RexUserAuthenticationBuilder;
import com.zmcsoft.rex.api.user.entity.User;
import com.zmcsoft.rex.api.user.service.UserService;
import org.hswebframework.utils.StringUtils;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.AuthenticationInitializeService;

/**
 * @author zhouhao
 * @since 1.0
 */
public class RexUserAuthenticationInitializeService implements AuthenticationInitializeService {

    private UserService userService;

    public RexUserAuthenticationInitializeService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication initUserAuthorization(String userId) {
        User user = userService.getById(userId);
        if (null == user) {
            return null;
        }
        // TODO: 17-10-20 应该可配置
        return RexUserAuthenticationBuilder.me(user)
                .addPermission(PermissionBuilder.me("test").actions("add").build())
                .bulid();
    }
}
