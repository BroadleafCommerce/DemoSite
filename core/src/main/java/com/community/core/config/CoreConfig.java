/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package com.community.core.config;

import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

/**
 * @author Jeff Fischer
 */
@Configuration
@ComponentScan(value = "com.broadleafcommerce.demo",
    excludeFilters = @Filter(type = FilterType.REGEX, pattern = "com.broadleafcommerce.demo.admin.*"))
@ComponentScan("com.community.core")
public class CoreConfig {

    /**
     * Setup the "blPU" entity manager on the request thread using the entity-manager-in-view pattern
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean openEntityManagerInViewFilterFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        OpenEntityManagerInViewFilter openEntityManagerInViewFilter = new OpenEntityManagerInViewFilter();
        registrationBean.setFilter(openEntityManagerInViewFilter);
        registrationBean.setName("openEntityManagerInViewFilter");
        registrationBean.setOrder(FilterOrdered.PRE_SECURITY_HIGH);
        return registrationBean;
    }

    /**
     * Basic http filter used at runtime
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean resourceUrlEncodingFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        ResourceUrlEncodingFilter resourceUrlEncodingFilter = new ResourceUrlEncodingFilter();
        registrationBean.setFilter(resourceUrlEncodingFilter);
        registrationBean.setOrder(FilterOrdered.POST_SECURITY_HIGH);
        return registrationBean;
    }

}
