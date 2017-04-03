package com.community.api;

import org.broadleafcommerce.common.config.EnableBroadleafSiteRootAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {SessionAutoConfiguration.class} )
public class ApiApplication extends SpringBootServletInitializer {

    @Configuration
    @EnableBroadleafSiteRootAutoConfiguration
    public static class BroadleafFrameworkConfiguration {}

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApiApplication.class);
    }
}
