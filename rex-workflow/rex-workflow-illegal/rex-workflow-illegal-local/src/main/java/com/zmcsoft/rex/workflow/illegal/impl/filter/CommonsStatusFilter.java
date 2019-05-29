package com.zmcsoft.rex.workflow.illegal.impl.filter;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.workflow.illegal.api.filter.IllegalCaseFilter;
import com.zmcsoft.rex.workflow.illegal.api.service.PropertyRefactor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CommonsStatusFilter implements IllegalCaseFilter {

    @Autowired
    protected PropertyRefactor refactor;

    private static List<String> allowDriverStatus = Arrays.asList("A", "H", "I");

    private static List<String> allowCarUseNature = Arrays.asList("A","G","H","I","K","M","Q","P","L");

    private static List<String> allowCarType = Arrays.asList(
            "K33", "H31", "H32", "H33", "H34", "H35", "H37",
            "H38", "H39", "H3A", "H3B", "H3C", "H3D", "H3F", "H3G", "H41",
            "H42", "H43", "H44", "H45", "H46", "H47", "H4A", "H4B", "H4C",
            "H4F", "H4G", "H51", "H52", "H53", "H54", "H55", "H5B", "H5C",
            "K31", "K32", "K34", "K38", "K39", "K41", "K42", "K43", "K49"
    );

    @Override
    public FilterResult filter(UserCar car, UserDriverLicense license, CarIllegalCase illegalCase) {
        String driverLicenseStatus = license.getLicenseStatus();
        String carUseNature = car.getUseNature();
        String trafficMode = illegalCase.getTrafficMode();


//        String carType = car.getCarType();

        if (null != trafficMode) {
            List<String> dr = Arrays.asList(trafficMode.split(""));
            if (anyMatch(allowCarType, dr)) {
                return IllegalCaseFilter.buildResult(6, "你的机动车状态，不属于业务办理范围！！");
            }
        }

        if (null != driverLicenseStatus) {
            List<String> dr = Arrays.asList(driverLicenseStatus.split(""));
            if (!anyMatch(allowDriverStatus, dr)) {
                return IllegalCaseFilter.buildResult(5, "您的驾驶证状态，不属于业务办理范围！");
            }
        }

        if (null != carUseNature) {
            List<String> dr = Arrays.asList(carUseNature.split(""));
            if (!anyMatch(allowCarUseNature, dr)) {
                return IllegalCaseFilter.buildResult(7, "限制营运类车辆办理");
            }
        }

        if (!refactor.applyPlateNumber(illegalCase.getPlateNumber()).startsWith("川A")) {
            return IllegalCaseFilter.buildResult(2, "经查询无成都籍机动车信息！");
        }
        return null;
    }

    public static boolean anyMatch(List<String> from, List<String> to) {
        return to.stream().anyMatch(from::contains);
    }

    public static boolean allMatch(List<String> from, List<String> to) {
        return to.stream().allMatch(from::contains);
    }
}
