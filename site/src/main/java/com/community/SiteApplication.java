package com.community;

import org.broadleafcommerce.common.config.EnableBroadleafSiteAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableAutoConfiguration
public class SiteApplication {

    @Configuration
    @EnableBroadleafSiteAutoConfiguration
    public static class BroadleafFrameworkConfiguration {}
    
    public static void main(String[] args) {
        SpringApplication.run(SiteApplication.class, args);
    }
    
}
