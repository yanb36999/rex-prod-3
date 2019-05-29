package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.alibaba.fastjson.JSON;
import com.zmcsoft.rex.workflow.illegal.api.service.PayVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.hswebframework.web.authorization.oauth2.client.OAuth2RequestService;
import org.hswebframework.web.authorization.oauth2.client.response.OAuth2Response;
import org.hswebframework.web.controller.message.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j(topic = "business.pay.verif")
public class OAuth2PayVerificationService implements PayVerificationService {

    @Autowired
    private OAuth2RequestService oAuth2RequestService;

    @Value("${icbc.pay.oauth2.id:icbc-server-test}")
    private String serviceId = "icbc-server-test";

    @Override
    public VerificationResult<BigDecimal> getRealPayAmount(String decisionNo) {
        VerificationResult<BigDecimal> verificationResult;
        try {
            OAuth2Response response = oAuth2RequestService.create(serviceId)
                    .byClientCredentials()
                    .request("icbc/query/" + decisionNo)
                    .get();

            String strResult = response.asString();
            ResponseMessage result = JSON.parseObject(strResult, ResponseMessage.class);
            log.info("调用付款金额接口结果:\n{}", strResult);

            if (result.getStatus() == 200) { //oauth2 请求成功
                strResult = String.valueOf(result.getResult());
                String[] data = strResult.split("[|]");
                if (CodeMapping.of(data[0]) != CodeMapping.success) {
                    verificationResult = VerificationResult.<BigDecimal>builder()
                            .success(false)
                            .codeMapping(CodeMapping.of(data[0]))
                            .build();
                } else {
                    verificationResult = VerificationResult.<BigDecimal>builder()
                            .success(true)
                            .codeMapping(CodeMapping.success)
                            .result(new BigDecimal(data[6]).add(new BigDecimal(data[7])))
                            .build();
                }

            } else { //oauth2返回失败信息
                verificationResult = VerificationResult.<BigDecimal>builder()
                        .success(false)
                        .codeMapping(CodeMapping.requestError)
                        .build();

            }
            verificationResult.setMessage(strResult);
        } catch (Exception e) { //调用异常
            log.error("调用获取缴费金额接口错误", e);
            verificationResult = VerificationResult
                    .<BigDecimal>builder()
                    .codeMapping(CodeMapping.requestError)
                    .success(false)
                    .build();
            verificationResult.setMessage(e.getMessage());
        }

        return verificationResult;
    }

    public VerificationResult<Boolean> paySuccess(String decisionNo, BigDecimal amount) {
        VerificationResult<Boolean> verificationResult;
        try {
            OAuth2Response response = oAuth2RequestService.create(serviceId)
                    .byClientCredentials()
                    .request("icbc/pay/" + decisionNo)
                    .param("amount", amount)
                    .post();
            String strResult = response.asString();

            ResponseMessage result = JSON.parseObject(strResult, ResponseMessage.class);
            log.info("调用付款接口结果:\n{}", strResult);

            if (result.getStatus() == 200) {
                strResult = String.valueOf(result.getResult());
                String[] data = strResult.split("[|]");
                if (CodeMapping.of(data[0]) == CodeMapping.success) {
                    verificationResult = VerificationResult.<Boolean>builder()
                            .success(true)
                            .result(true)
                            .codeMapping(CodeMapping.success)
                            .build();
                } else {
                    verificationResult = VerificationResult.<Boolean>builder()
                            .codeMapping(CodeMapping.of(data[0]))
                            .success(false)
                            .build();
                    log.error("调用付款接口失败{}:{} {}", decisionNo, amount, strResult);
                }
            } else {
                verificationResult = VerificationResult.<Boolean>builder()
                        .codeMapping(CodeMapping.requestError)
                        .success(false)
                        .build();
                log.error("调用付款接口失败{}:{} {}", decisionNo, amount, result.getMessage());
            }
            verificationResult.setMessage(strResult);
        } catch (Exception e) {
            log.error("调用付款接口失败{}:{}", decisionNo, amount, e);
            verificationResult = VerificationResult.<Boolean>builder()
                    .codeMapping(CodeMapping.requestError)
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
        return verificationResult;
    }

}
