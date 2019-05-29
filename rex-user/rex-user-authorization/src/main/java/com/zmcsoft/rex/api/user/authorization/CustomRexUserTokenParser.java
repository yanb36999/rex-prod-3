package com.zmcsoft.rex.api.user.authorization;

/**
 * 自定义用户令牌解析,通过令牌获取用户id
 *
 * @author zhouhao
 * @since 1.0
 */
public interface CustomRexUserTokenParser {

    /**
     * 根据令牌获取用户id
     *
     * @param token 从请求中解析出的令牌
     * @return 用户的id, 不存在返回<code>null</code>
     */
    String parseUserId(String token);
}
