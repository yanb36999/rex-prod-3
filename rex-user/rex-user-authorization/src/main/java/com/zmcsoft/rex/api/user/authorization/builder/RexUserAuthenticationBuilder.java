package com.zmcsoft.rex.api.user.authorization.builder;

import com.zmcsoft.rex.api.user.entity.User;
import org.hswebframework.web.authorization.Authentication;
import org.hswebframework.web.authorization.Permission;
import org.hswebframework.web.authorization.Role;
import org.hswebframework.web.authorization.simple.SimpleAuthentication;
import org.hswebframework.web.authorization.simple.SimpleRole;
import org.hswebframework.web.authorization.simple.SimpleUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhouhao
 * @since
 */
public class RexUserAuthenticationBuilder {

    private User user;

    private List<Role> roles = new ArrayList<>();

    private List<Permission> permissions = new ArrayList<>();

    public static RexUserAuthenticationBuilder me(User user) {
        RexUserAuthenticationBuilder builder = new RexUserAuthenticationBuilder();
        builder.user = user;
        return builder;
    }

    public RexUserAuthenticationBuilder addRole(String roleId, String name) {
        roles.add(new SimpleRole(roleId, name));
        return this;
    }

    public RexUserAuthenticationBuilder addPermission(Permission permission) {
        permissions.add(permission);
        return this;
    }

    public RexUserAuthenticationBuilder text(String configText) {

        return this;
    }

    public Authentication bulid() {
        SimpleAuthentication authentication = new SimpleAuthentication();

        SimpleUser simpleUser = SimpleUser.builder()
                .id(user.getId())
                .username(user.getIdCard())
                .name(user.getName())
                .build();

        authentication.setUser(simpleUser);

        authentication.setRoles(roles);
        authentication.setPermissions(permissions);

        return authentication;
    }
}
