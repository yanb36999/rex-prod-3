package com.zmcsoft.rex.workflow.illegal.api;

import java.util.Arrays;

public enum  DriverLicenseErrStatus {


    NORMAL("A","正常"),
    ILLEGAL_NO_DSP("H","违法未处理"),
    ACCIDENT_NO_DSP("I","事故未处理");


    private String code;

    private String message;

    DriverLicenseErrStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public static DriverLicenseErrStatus ofCode(String code) {
        return Arrays.stream(values()).filter(status -> status.code().equals(code))
                .findFirst().orElse(null);
    }
}
