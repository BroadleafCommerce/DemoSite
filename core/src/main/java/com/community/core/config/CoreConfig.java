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
