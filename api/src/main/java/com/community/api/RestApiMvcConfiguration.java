package com.community.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.broadleafcommerce.rest.api.BroadleafRestApiMvcConfiguration;


@Configuration
@EnableWebMvc
@ComponentScan("com.community.api")
public class RestApiMvcConfiguration extends BroadleafRestApiMvcConfiguration {
    
}
