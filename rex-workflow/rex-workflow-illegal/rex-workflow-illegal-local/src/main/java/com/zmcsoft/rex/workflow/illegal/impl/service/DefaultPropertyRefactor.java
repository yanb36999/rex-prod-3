package com.zmcsoft.rex.workflow.illegal.impl.service;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.workflow.illegal.api.entity.IllegalCode;
import com.zmcsoft.rex.workflow.illegal.api.service.DictService;
import com.zmcsoft.rex.workflow.illegal.api.service.IllegalCodeService;
import com.zmcsoft.rex.workflow.illegal.api.service.PropertyRefactor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by zhouhao on 2017/10/29.
 */
@Service
public class DefaultPropertyRefactor implements PropertyRefactor {
    @Autowired
    private DictService dictService;

    @Autowired
    private IllegalCodeService illegalCodeService;

    @Override
    public void applyDict(String id, Supplier<String> getter, Consumer<String> setter) {
        String in = getter.get();
        if (in == null) {
            setter.accept(null);
            return;
        }
        setter.accept(dictService.getString(id, in, in));
    }

    @Override
    public void applyDict(String id, Supplier<String> getter, Consumer<String> setter, String split, String delimiter) {
        if (split == null) {
            split = "";
        }
        String in = getter.get();
        if (in == null) {
            setter.accept(null);
            return;
        }
        setter.accept(Arrays.stream(in.split(split))
                .map(s -> dictService.getString(id, s.trim(), s.trim()))
                .reduce((s1, s2) -> s1 + delimiter + s2)
                .orElse(in));
    }

//    @Override
//    public void applyDict(String id, Supplier<String> getter, Consumer<String> setter, String split) {
//        if(split==null){
//            split="";
//        }
//        String in = getter.get();
//        if(in==null){
//            setter.accept(null);
//            return;
//        }
//        setter.accept(Arrays.stream(in.split(split))
//                .map(s -> dictService.getString(id, s.trim(), s.trim()))
//                .reduce((s1,s2)->s1+";"+s2)
////                .reduce(String::join)
//                .orElse(in));
//    }

    @Override
    public CarIllegalCase applyCaseText(CarIllegalCase illegalCase) {
        applyPlateNumber(illegalCase::getPlateNumber, illegalCase::setPlateNumber);
        applyDict("plate-type", illegalCase::getPlateType, illegalCase::setPlateTypeText);
        IllegalCode code = illegalCodeService.getByCode(illegalCase.getIllegalActivities());
        if (code == null) {
            illegalCase.setIllegalActivitiesText("");
        } else {
            illegalCase.setIllegalActivitiesText(code.getContentAbbreviate());
        }
        return illegalCase;
    }

    public static void main(String[] args) {
        DefaultPropertyRefactor refactor = new DefaultPropertyRefactor();

        System.out.println(refactor.applyPlateNumber("川ABC123"));
        System.out.println(refactor.applyPlateNumber("ABC123"));
        System.out.println(refactor.applyPlateNumber("京ABC123"));
        System.out.println(refactor.applyPlateNumber("京ABC13"));
        System.out.println(refactor.applyPlateNumber("ABC23"));

        System.out.println(refactor.removePlateNumber("川ABC123"));
        System.out.println(refactor.removePlateNumber("ABC123"));
        System.out.println(refactor.removePlateNumber("京ABC123"));
        System.out.println(refactor.removePlateNumber("京ABC13"));
        System.out.println(refactor.removePlateNumber("ABC23"));

    }

}
