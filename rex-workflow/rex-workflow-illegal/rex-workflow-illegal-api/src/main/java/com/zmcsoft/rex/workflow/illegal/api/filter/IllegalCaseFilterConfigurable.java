package com.zmcsoft.rex.workflow.illegal.api.filter;

import java.util.List;

public interface IllegalCaseFilterConfigurable {
    String getFilterName();

    void config(FilterConfiguration configuration);

    List<ConfigurationProperty> getConfigProperty();

    interface ConfigurationProperty {
        
        static ConfigurationProperty of(String property,Class type, String name, String comment, boolean require) {
            return new ConfigurationProperty() {
                @Override
                public String getProperty() {
                    return property;
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getComment() {
                    return comment;
                }

                @Override
                public boolean isRequire() {
                    return require;
                }

                @Override
                public Class getType() {
                    return type;
                }
            };
        }

        Class getType();

        String getProperty();

        String getName();

        String getComment();

        boolean isRequire();
    }
}
