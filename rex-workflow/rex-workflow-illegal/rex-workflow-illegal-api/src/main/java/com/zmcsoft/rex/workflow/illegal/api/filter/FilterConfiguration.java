package com.zmcsoft.rex.workflow.illegal.api.filter;


public interface FilterConfiguration {
    <V> V get(String configName,V defaultValue);

    int getInt(String configName,int defaultValue);

    String getString(String configName,String defaultValue);

    boolean getBoolean(String configName,boolean defaultValue);

    double getDouble(String configName,double defaultValue);


}
