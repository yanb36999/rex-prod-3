package com.zmcsoft.rex.workflow.illegal.api.filter;

import org.hswebframework.utils.StringUtils;

import java.util.Map;
import java.util.Objects;

public class MapFilterConfiguration implements FilterConfiguration {

    private Map<String,Object> configuration;

    public MapFilterConfiguration(Map<String,Object> config){
        Objects.requireNonNull(config);
        this.configuration=config;
    }
    @Override
    public <V> V get(String configName, V defaultValue) {
        return (V) configuration.getOrDefault(configName,defaultValue);
    }

    @Override
    public int getInt(String configName, int defaultValue) {
        return StringUtils.toInt(get(configName,defaultValue));
    }

    @Override
    public String getString(String configName, String defaultValue) {
        return String.valueOf(get(configName,defaultValue));
    }

    @Override
    public boolean getBoolean(String configName, boolean defaultValue) {
        return StringUtils.isTrue(get(configName,defaultValue));
    }

    @Override
    public double getDouble(String configName, double defaultValue) {
        return StringUtils.toDouble(get(configName,defaultValue));
    }
}
