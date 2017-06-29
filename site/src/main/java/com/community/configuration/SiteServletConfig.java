package com.community.configuration;

import org.broadleafcommerce.cms.web.PageHandlerMapping;
import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.broadleafcommerce.common.web.controller.annotation.EnableAllFrameworkControllers;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.common.web.filter.IgnorableOpenEntityManagerInViewFilter;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@EnableAllFrameworkControllers
@ComponentScan("com.community.controller")
public class SiteServletConfig extends WebMvcConfigurerAdapter {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/favicon.ico");
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
        mappings.put("/img/**", "blImageResources");
        mappings.put("/fonts/**", "blFontResources");
        resourceMapping.setMappings(mappings);
        return resourceMapping;
    }
    
    @Merge("blJsLocations")
    public List<String> jsLocations() {
        return Collections.singletonList("classpath:/js/");
    }

    @Merge("blCssLocations")
    public List<String> cssLocations() {
        return Collections.singletonList("classpath:/css/");
    }

    @Merge("blImageLocations")
    public List<String> imageLocations() {
        return Collections.singletonList("classpath:/img/");
    }

    @Merge("blFontLocations")
    public List<String> fontLocations() {
        return Collections.singletonList("classpath:/fonts/");
    }
    
    @Bean
    public HandlerMapping productHandlerMapping() {
        ProductHandlerMapping mapping = new ProductHandlerMapping();
        mapping.setOrder(3);
        return mapping;
    }
    
    @Bean
    public HandlerMapping pageHandlerMapping() {
        PageHandlerMapping mapping = new PageHandlerMapping();
        mapping.setOrder(4);
        return mapping;
    }
    
    @Bean
    public HandlerMapping categoryHandlerMapping() {
        CategoryHandlerMapping mapping = new CategoryHandlerMapping();
        mapping.setOrder(5);
        return mapping;
    }
    
}
