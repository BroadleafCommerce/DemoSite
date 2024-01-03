/*-
 * #%L
 * Community Demo API
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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
import org.broadleafcommerce.common.web.controller.annotation.FrameworkRestController;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.common.web.filter.IgnorableOpenEntityManagerInViewFilter;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.configuration.SpringDocUIConfiguration;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.ObjectFactory;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import com.broadleafcommerce.rest.api.BroadleafRestApiMvcConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@ComponentScan("com.blcdemo.api")
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
    @Configuration
    @ConditionalOnProperty(name = "blc.api.doc.autogeneration.enabled", havingValue = "false", matchIfMissing = true)
    public static class SwaggerStaticConfig {
        @Bean
        SpringDocConfiguration springDocConfiguration(){
            return new SpringDocConfiguration();
        }

        @Bean
        SpringDocConfigProperties springDocConfigProperties() {
            return new SpringDocConfigProperties();
        }

        @Bean
        ObjectMapperProvider objectMapperProvider(SpringDocConfigProperties springDocConfigProperties){
            return new ObjectMapperProvider(springDocConfigProperties);
        }

        @Bean
        SpringDocUIConfiguration SpringDocUIConfiguration(Optional<SwaggerUiConfigProperties> optionalSwaggerUiConfigProperties){
            return new SpringDocUIConfiguration(optionalSwaggerUiConfigProperties);
        }
    }

    @Configuration
    @ConditionalOnProperty(name = "blc.api.doc.autogeneration.enabled", havingValue = "true")
    public static class SwaggerAutogenerationConfig {

        /**
         * Not sure if this is still needed, this is left from old springfox implementation
         *
         * @param webEndpointsSupplier
         * @param servletEndpointsSupplier
         * @param controllerEndpointsSupplier
         * @param endpointMediaTypes
         * @param corsProperties
         * @param webEndpointProperties
         * @param environment
         * @return
         */
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
            List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
            Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
            allEndpoints.addAll(webEndpoints);
            allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
            allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
            String basePath = webEndpointProperties.getBasePath();
            EndpointMapping endpointMapping = new EndpointMapping(basePath);
            boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
            return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping);
        }

        protected boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
            return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
        }

        /**
         * This is override of springdoc bean(defined in some springdoc config class), to add FrameworkRestController
         * beans for generation
         *
         * @param openAPIBuilderObjectFactory
         * @param requestBuilder
         * @param responseBuilder
         * @param operationParser
         * @param springDocConfigProperties
         * @param springDocProviders
         * @param springDocCustomizers
         * @return
         */
        @Bean
        OpenApiWebMvcResource openApiResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder,
                                              GenericResponseService responseBuilder, OperationService operationParser,
                                              SpringDocConfigProperties springDocConfigProperties,
                                              SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers) {
            return new BroadleafOpenApiWebMvcResource(openAPIBuilderObjectFactory, requestBuilder,
                    responseBuilder, operationParser, springDocConfigProperties, springDocProviders, springDocCustomizers);
        }

        /**
         * Define generic OpenAPI info - like titles, version, description etc
         * Added basicAuth just in case(it will be shown as button in swagger)
         *
         * @return
         */
        @Bean
        public OpenAPI broadleafOpenAPI() {
            return new OpenAPI()
                    .components(
                            new Components()
                                    .addSecuritySchemes("basicAuth",
                                            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
                                    )
                    ).security(Arrays.asList(new SecurityRequirement().addList("basicAuth")))
                    .info(new Info().title("Broadleaf Commerce API")
                            .description("The default Broadleaf Commerce APIs")
                            .version("v1")
                    );
        }

        /**
         * Not sure if we need this, left from the springfox
         * Seems it gives possibility to translate operation descriptions according to i18n properties/resources/messages
         *
         * @return
         */
        @Bean
        public TranslationOperationBuilderPlugin translationPlugin() {
            return new TranslationOperationBuilderPlugin();
        }

        @Order(Ordered.LOWEST_PRECEDENCE)
        public static class TranslationOperationBuilderPlugin implements OperationCustomizer {
            @Autowired
            protected MessageSource translator;

            @Override
            public Operation customize(Operation operation, HandlerMethod handlerMethod) {
                operation.getResponses().forEach((
                        k, v) -> v.setDescription(translator.getMessage(v.getDescription(), null, v.getDescription(), Locale.getDefault()))
                );
                return operation;
            }
        }
        public static class BroadleafOpenApiWebMvcResource extends OpenApiWebMvcResource {
            public BroadleafOpenApiWebMvcResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder, GenericResponseService responseBuilder, OperationService operationParser, SpringDocConfigProperties springDocConfigProperties, SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers) {
                super(openAPIBuilderObjectFactory, requestBuilder,
                        responseBuilder, operationParser,  springDocConfigProperties, springDocProviders, springDocCustomizers);
            }

            @Override
            protected boolean isRestController(Map<String, Object> restControllers, HandlerMethod handlerMethod, String operationPath) {
                FrameworkRestController responseBodyAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), FrameworkRestController.class);
                return responseBodyAnnotation != null || super.isRestController(restControllers, handlerMethod, operationPath);
            }
        }
    }

}
