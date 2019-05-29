package com.zmcsoft.rex.user.local.service.simple;

import com.zmcsoft.rex.api.user.entity.User;
import com.zmcsoft.rex.api.user.service.UserService;
import com.zmcsoft.rex.user.local.dao.RexUserDao;
import org.hswebframework.web.datasource.annotation.UseDataSource;
import org.hswebframework.web.id.IDGenerator;
import org.hswebframework.web.service.DefaultDSLUpdateService;
import org.hswebframework.web.service.GenericEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("rexUserService")
@UseDataSource("userSysDataSource")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SimpleUserService extends GenericEntityService<User, String> implements UserService {

    @Autowired
    private RexUserDao userDao;

    @Override
    public User getById(String userId) {
        return super.selectByPk(userId);
    }

    @Override
    public String registerUser(User user) {
        return null;
    }

    @Override
    public void updateById(String id, User user) {
        super.updateByPk(id, user);
    }

    @Override
    public boolean changeUserStatus(String id, byte status) {
        int i = DefaultDSLUpdateService
                .createUpdate(userDao)
                .where("idCard", id)
                .set("status", status)
                .exec();
        return i != 0;
    }

    @Override
    public boolean changeUserType(String userId, byte type) {
        int i = DefaultDSLUpdateService
                .createUpdate(userDao)
                .where("idCard", userId)
                .set("type", "0")
                .exec();

        return i != 0;

    }

    @Override
    protected IDGenerator<String> getIDGenerator() {
        return null;
    }

    @Override
    public RexUserDao getDao() {
        return userDao;
    }
}
