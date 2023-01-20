/*-
 * #%L
 * Community Demo API
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
package com.community.api.configuration;

import org.broadleafcommerce.common.web.controller.annotation.EnableFrameworkRestControllers;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.common.web.filter.IgnorableOpenEntityManagerInViewFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.broadleafcommerce.rest.api.BroadleafRestApiMvcConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ComponentScan("com.community.api")
public class RestApiMvcConfiguration extends BroadleafRestApiMvcConfiguration {

    @Configuration
    @EnableFrameworkRestControllers
    public static class EnableBroadleafRestControllers {}

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
    
    /*********************************************
     * 
     * Below is required swagger configuration for the APIs with Basic Authentication. If you do not want to
     * use Swagger then remove everything below as well as the springfox dependency
     * 
     *********************************************
     */
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/images/favicon-32x32.png")
                .addResourceLocations("classpath:/META-INF/resources/webjars/images/favicon-32x32.png");

        registry.addResourceHandler("/images/favicon-16x16.png")
                .addResourceLocations("classpath:/META-INF/resources/webjars/images/favicon-16x16.png");
    }
    
    @EnableSwagger2
    @Configuration
    public static class SwaggerConfig {

        @Bean
        public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
                WebEndpointsSupplier webEndpointsSupplier,
                ServletEndpointsSupplier servletEndpointsSupplier,
                ControllerEndpointsSupplier controllerEndpointsSupplier,
                EndpointMediaTypes endpointMediaTypes,
                CorsEndpointProperties corsProperties,
                WebEndpointProperties webEndpointProperties,
                Environment environment
        ) {
            List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
            Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
            allEndpoints.addAll(webEndpoints);
            allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
            allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
            String basePath = webEndpointProperties.getBasePath();
            EndpointMapping endpointMapping = new EndpointMapping(basePath);
            boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
            return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
        }

        protected boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
            return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
        }

        @Bean
        public Docket globalApi() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .select()
                      .apis(RequestHandlerSelectors.any())
                      .paths(PathSelectors.any())
                      .build()
                    .securitySchemes(Arrays.asList(new BasicAuth("basicAuth")))
                    .securityContexts(Arrays.asList(securityContext()))
                    .useDefaultResponseMessages(false)
                    .apiInfo(apiInfo());
        }
        
        private SecurityContext securityContext() {
            return SecurityContext.builder()
                .securityReferences(basicAuth())
                .forPaths(PathSelectors.any())
                .build();
        }
    
        private List<SecurityReference> basicAuth() {
            return Arrays.asList(SecurityReference.builder()
                .reference("basicAuth")
                .scopes(new AuthorizationScope[0])
                .build());
        }
    
    
        protected ApiInfo apiInfo () {
            return new ApiInfoBuilder().title("Broadleaf Commerce API")
                    .description("The default Broadleaf Commerce APIs")
                    .version("v1")
                    .build();
        }
    
        @Bean
        public TranslationOperationBuilderPlugin translationPlugin() {
            return new TranslationOperationBuilderPlugin();
        }
        
        @Order(Ordered.LOWEST_PRECEDENCE)
        public static class TranslationOperationBuilderPlugin implements OperationBuilderPlugin {
    
            @Autowired
            protected MessageSource translator;
            
            @Override
            public boolean supports(DocumentationType delimiter) {
                return true;
            }
    
            @Override
            public void apply(OperationContext context) {
                Set<ResponseMessage> messages = context.operationBuilder().build().getResponseMessages();
                Set<ResponseMessage> translated = new HashSet<>();
                for (ResponseMessage untranslated : messages) {
                    String translation = translator.getMessage(untranslated.getMessage(), null, untranslated.getMessage(), null);
                    translated.add(new ResponseMessage(untranslated.getCode(),
                        translation,
                        untranslated.getResponseModel(),
                        untranslated.getHeaders(),
                        untranslated.getVendorExtensions()));
                }
                context.operationBuilder().responseMessages(translated);
            }
            
        }
    }

}
