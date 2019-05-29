package com.zmcsoft.rex.workflow.illegal.api;

import java.util.Arrays;

/**
 * 举报状态
 */
public enum ReportStatus {

//    CHECK_ERR("1201","复核失败"),

    CHECK_ERR("1102","复核失败"),
    CHECk_OK("1202","复核通过"),//----新举报
    INFORM_OK("1203","已通知客户"),
    CONFIRM_Ok("1204","已确认"),//----历史举报
    COMPLETE_Ok("1205","已完成");


    private String code;

    private String message;

    ReportStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public static ReportStatus ofCode(String code) {
        return Arrays.stream(values()).filter(status -> status.code().equals(code))
                .findFirst().orElse(null);
    }
}
