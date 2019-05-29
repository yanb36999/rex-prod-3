package com.zmcsoft.rex.api.user.service;

import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import org.hswebframework.web.service.CrudService;

/**
 * @author zhouhao
 * @since 1.0
 */
public interface UserDriverLicenseService extends CrudService<UserDriverLicense,String>{

    UserDriverLicense getByUserId(String id);

    String saveOrUpdate(UserDriverLicense userDriverLicense);

    boolean changeUserDriverLicenseStatus(String id, Byte status);
}
