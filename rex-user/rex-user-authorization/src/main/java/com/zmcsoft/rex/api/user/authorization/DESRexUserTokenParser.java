package com.zmcsoft.rex.api.user.authorization;


import com.zmcsoft.rex.api.user.authorization.utils.DESUtils;
import org.apache.commons.codec.binary.Base64;

/**
 * 使用des加密的用户令牌解析器,token支持 userId或者openId
 *
 * @author zhouhao
 * @since 1.0
 */
public class DESRexUserTokenParser implements CustomRexUserTokenParser {
    private String secretKey;

    @Override
    public String parseUserId(String token) {
        try {
            //使用des解密
            byte[] result = DESUtils.decrypt(Base64.decodeBase64(token), secretKey);
            return new String(result);
        } catch (Exception ignore) {
            return null;
        }
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
