package com.community.admin;

import org.broadleafcommerce.common.config.EnableBroadleafAdminAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
//The exclude of ValidationAutoConfiguration is a temporary fix for https://github.com/spring-projects/spring-boot/issues/8495
@EnableAutoConfiguration(exclude = {SessionAutoConfiguration.class, ValidationAutoConfiguration.class, MultipartAutoConfiguration.class} )
public class AdminApplication extends SpringBootServletInitializer {

 @Configuration
 @EnableBroadleafAdminAutoConfiguration
 public static class BroadleafFrameworkConfiguration {}

 public static void main(String[] args) {
     SpringApplication.run(AdminApplication.class, args);
 }
 
 @Override
 protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
     return application.sources(AdminApplication.class);
 }

}

