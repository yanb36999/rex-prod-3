package com.zmcsoft.rex.workflow.illegal.api;

import java.util.Arrays;

/**
 * 车辆状态
 */
public enum IllegalCarStatus {

    CHECK_OK("1101", "复核通过"),
    CHECK_FAIL("1102", "复核不通过"),
    ENTER_NO("2101", "已通知被举报人"),
    ENTER_OK_W("2102", "已处理,微信"),//所有违法处理已完成
    ENTER_OK_X("2103", "已处理,现场"),
    ENTER_OK_M("2104","已处理，综合办理"),
    PUNISH_NO("3102","处理未完毕"),//部分违法处理完成
    PUNISH_OK("3103","已处罚");//群众线下处理返回到外网

    private String code;

    private String message;

    IllegalCarStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public static IllegalCarStatus ofCode(String code) {
        return Arrays.stream(values()).filter(status -> status.code().equals(code))
                .findFirst().orElse(null);
    }

    public boolean eq(String code){
        return  code().equals(code);
    }
}
