/*-
 * #%L
 * Community Demo Admin
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package com.community.admin.configuration;

import org.broadleafcommerce.common.web.controller.FrameworkControllerHandlerMapping;
import org.broadleafcommerce.common.web.controller.annotation.EnableAllFrameworkControllers;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.common.web.filter.IgnorableOpenEntityManagerInViewFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@EnableAllFrameworkControllers
public class AdminServletConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.setOrder(FrameworkControllerHandlerMapping.REQUEST_MAPPING_ORDER-1);
        registry.addResourceHandler("/img/**")
            .addResourceLocations("classpath:/open_admin_style/img/", "classpath:/common_style/img/", "classpath:/community-demo-style/img/", "/img/");
        registry.addResourceHandler("/fonts/**")
            .addResourceLocations("classpath:/open_admin_style/fonts/", "classpath:/community-demo-style/fonts/", "classpath:/common_style/fonts/");
        registry.addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/favicon.ico");
        registry.addResourceHandler("/robots.txt")
            .addResourceLocations("classpath:/robots.txt");
    }
    
    /**
     * Setup the "blPU" entity manager on the request thread using the entity-manager-in-view pattern
     */
    @Bean
    public FilterRegistrationBean openEntityManagerInViewFilterFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        OpenEntityManagerInViewFilter openEntityManagerInViewFilter = new IgnorableOpenEntityManagerInViewFilter();
        registrationBean.setFilter(openEntityManagerInViewFilter);
        registrationBean.setName("openEntityManagerInViewFilter");
        registrationBean.setOrder(FilterOrdered.PRE_SECURITY_HIGH);
        return registrationBean;
    }
    
    @Bean
    public HttpSessionEventPublisher sessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    
    @Bean
    public HandlerMapping staticResourcesHandlerMapping() {
        SimpleUrlHandlerMapping resourceMapping = new SimpleUrlHandlerMapping();
        resourceMapping.setOrder(-10);
        Properties mappings = new Properties();
        mappings.put("/js/**", "blJsResources");
        mappings.put("/css/**", "blCssResources");
        resourceMapping.setMappings(mappings);
        return resourceMapping;
    }
    
}
