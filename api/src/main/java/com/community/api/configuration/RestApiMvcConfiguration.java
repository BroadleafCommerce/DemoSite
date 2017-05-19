package com.community.api.configuration;

import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.broadleafcommerce.common.web.filter.IgnorableOpenEntityManagerInViewFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.broadleafcommerce.rest.api.BroadleafRestApiMvcConfiguration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
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


@Configuration
@EnableWebMvc
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@ComponentScan("com.community.api")
public class RestApiMvcConfiguration extends BroadleafRestApiMvcConfiguration {
    
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
    public Docket globalApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                  .apis(RequestHandlerSelectors.any())
                  .paths(PathSelectors.any())
                  .build()
                .pathMapping("/api/v1")
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
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = new AuthorizationScopeBuilder()
            .scope("")
            .build();
        return Arrays.asList(SecurityReference.builder()
            .reference("basicAuth")
            .scopes(authorizationScopes)
            .build());
    }


    protected ApiInfo apiInfo () {
        return new ApiInfoBuilder().title("Broadleaf Commerce API")
                .description("The default Broadleaf Commerce APIs")
                .version("v1")
                .build();
    }

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
