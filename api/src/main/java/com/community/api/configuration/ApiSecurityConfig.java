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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.web.core.security.RestApiCustomerStateFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelDecisionManagerImpl;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import java.util.Arrays;

import jakarta.servlet.Filter;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class ApiSecurityConfig {

    private static final Log LOG = LogFactory.getLog(ApiSecurityConfig.class);

    @Value("${asset.server.url.prefix.internal}")
    protected String assetServerUrlPrefixInternal;

    @Value("${server.port:8445}")
    private int httpsRedirectPort;

    @Value("${http.server.port:8082}")
    protected int httpServerPort;

    @Bean(name = "blAuthenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/swagger-ui.html",
                "/api/*/swagger-ui.html",
                "/swagger-ui*/**",
                "/api/*/swagger-ui*/**",
                "/api/*/swagger-resources/*",
                //this is default url where docs are generated
                "/api/*/v3/api-docs",
                "/api/*/v3/api-docs.yaml",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml",
                "/api/*/api-docs.yaml",
                "/api-docs.yaml"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(req -> req.anyRequest().permitAll())
            .sessionManagement(session -> {
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                session.sessionFixation().none();
                session.enableSessionUrlRewriting(false);
            })
            .requiresChannel(channel -> channel.anyRequest().requires(ChannelDecisionManagerImpl.ANY_CHANNEL))
            .portMapper(
                    mapper -> mapper
                            .http(httpServerPort).mapsTo(httpsRedirectPort)
            )
            .addFilterAfter(apiCustomerStateFilter(), RememberMeAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public Filter apiCustomerStateFilter() {
        RestApiCustomerStateFilter restApiCustomerStateFilter = new RestApiCustomerStateFilter();
        restApiCustomerStateFilter.setExcludeUrlPatterns(Arrays.asList("/api/*/swagger*", "/api/*/swagger*/**", "/swagger*", "/swagger*/**", "/**/swagger*/**"));
        return restApiCustomerStateFilter;
    }
}
