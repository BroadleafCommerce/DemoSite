/**
 * 
 */
package com.community.configuration;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class SiteServletContextInitializer implements ServletContextInitializer, WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(new org.springframework.web.context.request.RequestContextListener());
        servletContext.addListener(new org.springframework.security.web.session.HttpSessionEventPublisher());
        CharacterEncodingFilter encodingFilter = new org.springframework.web.filter.CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        servletContext.addFilter("encodingFilter", encodingFilter);
    }

}
