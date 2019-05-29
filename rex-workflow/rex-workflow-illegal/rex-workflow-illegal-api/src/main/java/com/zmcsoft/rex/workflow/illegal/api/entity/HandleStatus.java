package com.zmcsoft.rex.workflow.illegal.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author zhouhao
 * @since 1.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "案件处理状态")
public class HandleStatus {

    @Getter
    @Setter
    public static class HandleStatusCount extends HandleStatus {
        @ApiModelProperty("数量")
        private int total;

        public HandleStatusCount decrement() {
            total--;
            return this;
        }

        public HandleStatusCount increment() {
            total++;
            return this;
        }

    }

    public static HandleStatusCount ofCount(byte code, int total) {
        HandleStatus status = of(code);

        HandleStatusCount count = new HandleStatusCount();
        count.total = total;
        count.code = code;
        count.text = status.text;
        count.comment = status.comment;

        return count;
    }

    @ApiModelProperty("状态文本")
    protected String text;

    @ApiModelProperty("状态码")
    protected byte code;

    @ApiModelProperty("状态说明")
    protected String comment;


    public static HandleStatus of(int code) {
        for (HandleStatusDefine define : HandleStatusDefine.values()) {
            if (define.status.code == code) {
                return define.status;
            }
        }
        return HandleStatusDefine.UNKNOWN.status;
    }

    public static Byte[] allCode() {
        return Arrays.stream(HandleStatusDefine.values())
                .map(HandleStatusDefine::getCode).toArray(Byte[]::new);
    }
}
