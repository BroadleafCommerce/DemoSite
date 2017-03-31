package com.community.core.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Jeff Fischer
 */
public class StringFactoryBean implements FactoryBean<String> {

    @Value("${jmx.app.name}")
    protected String jmxAppName;

    @Override
    public String getObject() throws Exception {
        return jmxAppName;
    }

    @Override
    public Class<?> getObjectType() {
        return String.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
