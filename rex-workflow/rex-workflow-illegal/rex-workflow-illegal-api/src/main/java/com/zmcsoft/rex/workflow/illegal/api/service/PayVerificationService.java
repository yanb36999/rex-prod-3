package com.zmcsoft.rex.workflow.illegal.api.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public interface PayVerificationService {

    /**
     * 获取真实的缴费金额
     *
     * @param decisionNo 决定书编号
     * @return 请求结果
     */
    VerificationResult<BigDecimal> getRealPayAmount(String decisionNo);

    /**
     * 缴费成功操作
     *
     * @param decisionNo 处决书编号
     * @param amount     缴费的金额
     * @return 请求结果
     */
    VerificationResult<Boolean> paySuccess(String decisionNo, BigDecimal amount);

    enum CodeMapping {
        success("00", "成功"),
        //业务错误
        payed("01", "已缴费完毕"),
        paying("03", "缴费未完毕"),
        decisionNoExists("04", "决定书不存在"),
        payFailed("05", "交易失败"),
        //系统级错误
        sendError("-1", "发送报文失败"), //向工行接口发送报文失败，常见于socket超时
        requestError("-2", "发送请求失败")//向工行接口发送请求异常，常见于oauth2服务错误
        ,
        unknownError("-99", "未知错误");
        public final String code;

        public final String message;

        CodeMapping(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public static CodeMapping of(String code) {
            for (CodeMapping codeMapping : CodeMapping.values()) {
                if (codeMapping.code.equals(code)) {
                    return codeMapping;
                }
            }
            return unknownError;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class VerificationResult<T> {
        private boolean success;
        private CodeMapping codeMapping;
        private T result;
        private String message;
    }
}
