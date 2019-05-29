package com.zmcsoft.rex.api.user.authorization.builder;

import org.hswebframework.web.authorization.Permission;
import org.hswebframework.web.authorization.simple.SimplePermission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhouhao
 * @since
 */
public class PermissionBuilder extends SimplePermission {
    String id;

    Set<String> actions = new HashSet<>();

    public static PermissionBuilder me(String id) {
        return new PermissionBuilder().id(id);
    }

    public PermissionBuilder id(String id) {
        this.id = id;
        return this;
    }

    public PermissionBuilder actions(Set<String> actions) {
        this.actions = actions;
        return this;
    }

    public PermissionBuilder actions(String... actions) {
        this.actions.addAll(Arrays.asList(actions));
        return this;
    }

    public Permission build() {
        return SimplePermission.builder().id(getId()).actions(getActions()).build();
    }
}
