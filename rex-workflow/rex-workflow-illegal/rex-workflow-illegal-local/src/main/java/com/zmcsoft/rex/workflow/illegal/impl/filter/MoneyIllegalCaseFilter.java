package com.zmcsoft.rex.workflow.illegal.impl.filter;

import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import com.zmcsoft.rex.workflow.illegal.api.filter.FilterConfiguration;
import com.zmcsoft.rex.workflow.illegal.api.filter.IllegalCaseFilter;
import com.zmcsoft.rex.workflow.illegal.api.filter.IllegalCaseFilterConfigurable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j(topic = "business.filter.MoneyIllegalCaseFilter")
public class MoneyIllegalCaseFilter implements IllegalCaseFilter,IllegalCaseFilterConfigurable {

    private int maxPayMoney=200;

    @Override
    public FilterResult filter(UserCar car, UserDriverLicense license, CarIllegalCase illegalCase) {
        if(illegalCase==null||illegalCase.getPayMoney()==null){
            return IllegalCaseFilter.alwaysPass;
        }
        if(illegalCase.getPayMoney().intValue()>maxPayMoney){
            return new FilterResult() {
                @Override
                public boolean isPass() {
                    return false;
                }

                @Override
                public int getCode() {
                    return -1;
                }

                @Override
                public String getReason() {
                    return "罚款超过200";
                }
            };
        }
        return IllegalCaseFilter.alwaysPass;
    }

    @Override
    public String getFilterName() {
        return "PayMoney";
    }

    @Override
    public void config(FilterConfiguration configuration) {
        this.maxPayMoney=configuration.get("maxPayMoney",200);
        log.debug("设置最大罚款金额配置:{}",this.maxPayMoney);
    }

    @Override
    public List<ConfigurationProperty> getConfigProperty() {
        return Collections.singletonList(
                ConfigurationProperty.of(
                        "maxPayMoney", Integer.class,
                        "最大处罚金额",
                        "处罚金额超过最大处罚金额时，不能提交处罚申请",
                        true));
    }
}
