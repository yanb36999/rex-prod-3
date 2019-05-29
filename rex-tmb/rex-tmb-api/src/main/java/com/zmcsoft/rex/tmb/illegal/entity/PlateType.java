package com.zmcsoft.rex.tmb.illegal.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 号牌种类枚举
 *
 * @author zhouhao
 * @since 1.0
 */
public enum PlateType {
    UNKNOWN("", "未知"),
    T_01("01", "大型汽车"),
    T_02("02", "小型汽车"),
    T_03("03", "使馆汽车"),
    T_04("04", "领馆汽车"),
    T_05("05", "境外汽车"),
    T_06("06", "外籍汽车"),
    T_07("07", "两、三轮摩托车"),
    T_08("08", "轻便摩托车"),
    T_09("09", "使馆摩托车"),
    T_10("10", "领馆摩托车"),
    T_11("11", "境外摩托车"),
    T_12("12", "外籍摩托车"),
    T_13("13", "农用运输车类"),
    T_14("14", "拖拉机"),
    T_15("15", "挂车"),
    T_16("16", "教练汽车"),
    T_17("17", "教练摩托车"),
    T_18("18", "试验汽车"),
    T_19("19", "试验摩托车"),
    T_20("20", "临时入境汽车"),
    T_21("21", "临时入境摩托车"),
    T_22("22", "临时行驶车"),
    T_23("23", "警用汽车号牌"),
    T_24("24", "警用摩托车号牌"),
    T_25("25", "军用车辆号牌");
    private final String code;

    private final String text;

    PlateType(String code, String text) {
        this.code = code;
        this.text = text;
    }

    private static final Map<String, PlateType> mappingCode = new HashMap<>();

    static {
        for (PlateType plateType : PlateType.values()) {
            mappingCode.put(plateType.code, plateType);
        }
    }

    public static PlateType codeOf(String code) {
        return mappingCode.getOrDefault(code, UNKNOWN);
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
