package com.community.admin.configuration;

import org.broadleafcommerce.common.web.controller.FrameworkControllerHandlerMapping;
import org.broadleafcommerce.openadmin.web.controller.config.AdminWebMvcConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import java.util.Locale;
import java.util.Properties;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
public class AdminServletConfig extends AdminWebMvcConfiguration {

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
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleChangeInterceptor());
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setCookieHttpOnly(true);
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

}
