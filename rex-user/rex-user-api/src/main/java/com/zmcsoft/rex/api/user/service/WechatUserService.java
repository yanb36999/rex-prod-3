package com.zmcsoft.rex.api.user.service;

/**
 * 微信用户服务
 *
 * @author zhouhao
 * @since 1.0
 */
public interface WechatUserService {
    Long getUserIdByOpenId(String openId);
}
