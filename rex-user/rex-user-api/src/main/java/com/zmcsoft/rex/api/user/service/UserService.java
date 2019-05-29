package com.zmcsoft.rex.api.user.service;

import com.zmcsoft.rex.api.user.entity.User;
import org.hswebframework.web.service.CrudService;

/**
 * @author zhouhao
 * @since 1.0
 */
public interface UserService{

    String USER_AUTH_CACHE_NAME = "rex-user-auth-";

    User getById(String id);

    String registerUser(User user);

    void updateById(String id, User user);

    boolean changeUserStatus(String id, byte status);

    boolean changeUserType(String id, byte type);
}
