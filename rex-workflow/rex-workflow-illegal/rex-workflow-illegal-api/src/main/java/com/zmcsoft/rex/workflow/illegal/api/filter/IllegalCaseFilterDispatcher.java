package com.zmcsoft.rex.workflow.illegal.api.filter;


import com.zmcsoft.rex.api.user.entity.UserCar;
import com.zmcsoft.rex.api.user.entity.UserDriverLicense;
import com.zmcsoft.rex.tmb.illegal.entity.CarIllegalCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IllegalCaseFilterDispatcher {

    private List<IllegalCaseFilter> filters;

    @Autowired(required = false)
    public void setFilters(List<IllegalCaseFilter> filters) {
        this.filters = filters;
    }

    public IllegalCaseFilter.FilterResult doFilter(UserCar car, UserDriverLicense license, CarIllegalCase illegalCase) {
        if (filters != null) {
            return filters.stream()
                    .map(filter -> filter.filter(car, license, illegalCase))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(IllegalCaseFilter.alwaysPass);
        }
        return IllegalCaseFilter.alwaysPass;
    }

    public List<IllegalCaseFilterConfigurable> getAllFilter() {
        if (null != filters) {
            return filters.stream()
                    .filter(IllegalCaseFilterConfigurable.class::isInstance)
                    .map(IllegalCaseFilterConfigurable.class::cast)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void doConfiguration(String filterName, FilterConfiguration configuration) {
        if (null != filters) {
            filters.stream()
                    .filter(IllegalCaseFilterConfigurable.class::isInstance)
                    .map(IllegalCaseFilterConfigurable.class::cast)
                    .filter(configurable -> configurable.getFilterName().equalsIgnoreCase(filterName))
                    .forEach(configurable -> configurable.config(configuration));
        }
    }

    public void doConfiguration(String filterName, Map<String, Object> configuration) {
        doConfiguration(filterName, new MapFilterConfiguration(configuration));
    }
}
