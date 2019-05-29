package com.zmcsoft.rex.workflow.illegal.api.filter;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;

/**
 * 案件过滤器,通过过滤器筛选不能处理的案件
 *
 * @author zhouhao
 * @since 1.0
 */
public interface IllegalCaseFilter {

    FilterResult filter(UserCar car, UserDriverLicense license, CarIllegalCase illegalCase);

    FilterResult alwaysPass = new FilterResult() {
        @Override
        public boolean isPass() {
            return true;
        }

        @Override
        public int getCode() {
            return 0;
        }

        @Override
        public String getReason() {
            return "";
        }
    };

    static FilterResult buildResult(int code, String reason) {
        return new FilterResult() {
            @Override
            public boolean isPass() {
                return false;
            }

            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getReason() {
                return reason;
            }
        };
    }

    interface FilterResult {

        boolean isPass();

        int getCode();

        String getReason();

    }
}
