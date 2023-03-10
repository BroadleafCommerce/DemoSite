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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelDecisionManagerImpl;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import javax.servlet.Filter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

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

    @Bean(name = "blAuthenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                antMatcher("/api/**/webjars/**"),
                antMatcher("/api/**/images/favicon-*"),
                antMatcher("/api/**/jhawtcode/**"),
                antMatcher("/api/**/swagger-ui.html"),
                antMatcher("/api/**/swagger-resources/**"),
                antMatcher("/api/**/v2/api-docs")
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .mvcMatcher("/api/**")
                .httpBasic()
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/**")
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionFixation()
                .none()
                .enableSessionUrlRewriting(false)
                .and()
                .requiresChannel()
                .anyRequest()
                .requires(ChannelDecisionManagerImpl.ANY_CHANNEL)
                .and()
                .addFilterAfter(apiCustomerStateFilter(), RememberMeAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public Filter apiCustomerStateFilter() {
        return new RestApiCustomerStateFilter();
    }

}
