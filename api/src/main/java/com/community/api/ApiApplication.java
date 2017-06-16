package com.community.api;

import org.broadleafcommerce.common.config.EnableBroadleafSiteRootAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@SpringBootApplication
@EnableAutoConfiguration
public class ApiApplication {

    @Configuration
    @EnableBroadleafSiteRootAutoConfiguration
    public static class BroadleafFrameworkConfiguration {}

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
    
}
