package com.zmcsoft.rex.workflow.illegal.api.service;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 属性重构接口，用于重构各种对象的属性，以及字典转换。
 * 注意‼️：重构一般只用于在响应给客户端的时候，不应该在进行逻辑操作或者持久化的时候使用
 *
 * @author zhouhao
 * @since 1.0
 */
public interface PropertyRefactor {
    /**
     * 从参数getter中获取号牌，重构后通过参数的setter设置重构后的号牌
     * <pre>
     *     //A8888,重构后为川A8888
     *     //川A8888,重构后为川A8888
     *     propertyRefactor.applyPlateNumber(car::getPlatNumber,car::setPlatNumberText);
     * </pre>
     *
     * @param getter get方法
     * @param setter set方法
     */
    default void applyPlateNumber(Supplier<String> getter, Consumer<String> setter) {
        setter.accept(applyPlateNumber(getter.get()));
    }

    default  String applyPlateNumber(String in) {
        String out = in;

        if (in != null) {
            char c = in.charAt(0);
            if (!((c >= 0x4e00) && (c <= 0x9fbb)))
                out = "川" + in;
        }
        return out;
    }

    default String removePlateNumber(String in) {
        if (in != null) {
            char c = in.charAt(0);
            if ((c >= 0x4e00) && (c <= 0x9fbb))
                return in.substring(1, in.length());
        }
        return in;
    }


    default void removePlateNumber(Supplier<String> getter, Consumer<String> setter) {
        setter.accept(removePlateNumber(getter.get()));
    }


    /**
     * 从参数getter中获取字典的键，字典转换后调用参数中setter设置转换后的值，如果无法从字典中获取值，则不进行设置
     * <pre>
     *     propertyRefactor.applyDict("plate-type",car::getPlateType,car::setPlateType);
     * </pre>
     *
     * @param id     字典的id
     * @param getter get方法
     * @param setter set方法
     */
    void applyDict(String id, Supplier<String> getter, Consumer<String> setter);

    /**
     * 作用和{@link this#applyDict(String, Supplier, Consumer)}类似
     * 提供支持多个键的映射，指定分隔符，将对键进行分割，分别获取到字典值后，再拼接在一起
     * <pre>
     *     //B=转出 G=违法未处理
     *     // BG ->  转出;违法未处理
     *     propertyRefactor.applyDic("car-status",car::getCarStatus,car::setCarStatus,"");
     * </pre>
     *
     * @param id     字典id
     * @param getter get方法
     * @param setter set方法
     * @param split  分割符 {@link String#split(String)}
     */
    default void applyDict(String id, Supplier<String> getter, Consumer<String> setter, String split) {
        applyDict(id, getter, setter, split, ";");
    }


    void applyDict(String id, Supplier<String> getter, Consumer<String> setter, String split, String delimiter);

    /**
     * 重构案件相关属性
     *
     * @param illegalCase 案件
     * @return 重构后的案件，理论上和参数一致
     */
    CarIllegalCase applyCaseText(CarIllegalCase illegalCase);

    /**
     * 重构驾照属性
     *
     * @param license 驾照
     * @return 重构后的驾照
     */
    default UserDriverLicense applyLicenseText(UserDriverLicense license) {
        applyDict("driver-license", license::getLicenseStatus, license::setLicenseStatus);
        return license;
    }

    /**
     * 重构车辆属性
     *
     * @param car 车辆
     * @return 重构后的车辆
     */
    default UserCar applyCarText(UserCar car) {
        applyPlateNumber(car::getPlateNumber, car::setPlateNumber);
        applyDict("car-status", car::getCarStatus, car::setCarStatus, "");
        applyDict("car-type", car::getCarType, car::setCarType);
        applyDict("plate-type", car::getPlateType, car::setPlateTypeText);

        return car;
    }
}
