/*-
 * #%L
 * Community Demo Site
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
package com.community.configuration;

import org.broadleafcommerce.common.security.BroadleafAuthenticationFailureHandler;
import org.broadleafcommerce.common.security.handler.SecurityFilter;
import org.broadleafcommerce.core.web.order.security.BroadleafAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.Filter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@EnableWebSecurity
@ComponentScan({"org.broadleafcommerce.common.web.security", "org.broadleafcommerce.profile.web.core.security", "org.broadleafcommerce.core.web.order.security"})
@EnableMethodSecurity(securedEnabled = true)
public class SiteSecurityConfig {

    @Configuration
    public static class DependencyConfiguration {

        @Bean
        protected AuthenticationFailureHandler blAuthenticationFailureHandler(@Qualifier("blAuthenticationFailureRedirectStrategy") RedirectStrategy redirectStrategy) {
            BroadleafAuthenticationFailureHandler response = new BroadleafAuthenticationFailureHandler("/login?error=true");
            response.setRedirectStrategy(redirectStrategy);
            return response;
        }

        @Bean
        protected AuthenticationSuccessHandler blAuthenticationSuccessHandler(@Qualifier("blAuthenticationSuccessRedirectStrategy") RedirectStrategy redirectStrategy) {
            BroadleafAuthenticationSuccessHandler handler = new BroadleafAuthenticationSuccessHandler();
            handler.setRedirectStrategy(redirectStrategy);
            handler.setDefaultTargetUrl("/");
            handler.setTargetUrlParameter("successUrl");
            handler.setAlwaysUseDefaultTargetUrl(false);
            return handler;
        }

        @Bean
        protected Filter blCsrfFilter() {
            SecurityFilter securityFilter = new SecurityFilter();
            List<String> excludedRequestPatterns = new ArrayList<>();
            excludedRequestPatterns.add("/sample-checkout/**");
            excludedRequestPatterns.add("/hosted/sample-checkout/**");
            securityFilter.setExcludedRequestPatterns(excludedRequestPatterns);
            return securityFilter;
        }

    }

    @Value("${asset.server.url.prefix.internal}")
    protected String assetServerUrlPrefixInternal;

    @Resource(name = "blAuthenticationSuccessHandler")
    protected AuthenticationSuccessHandler successHandler;

    @Resource(name = "blAuthenticationFailureHandler")
    protected AuthenticationFailureHandler failureHandler;

    @Resource(name = "blCsrfFilter")
    protected Filter securityFilter;

    @Resource(name = "blUserDetailsService")
    protected UserDetailsService userDetailsService;

    @Resource(name = "blPasswordEncoder")
    protected PasswordEncoder passwordEncoder;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                antMatcher("/css/**"),
                antMatcher("/fonts/**"),
                antMatcher("/img/**"),
                antMatcher("/js/**"),
                antMatcher("/widget/js/**"),
                antMatcher("/**/" + assetServerUrlPrefixInternal + "/**"),
                antMatcher("/favicon.ico"));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean(name = "blAuthenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable().and()
                .sessionManagement()
                .sessionFixation()
                .migrateSession()
                .enableSessionUrlRewriting(false)
                .and()
                .formLogin()
                .permitAll()
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .loginPage("/login")
                .loginProcessingUrl("/login_post.htm")
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/account/wishlist/**", "/account/**")
                .authenticated()
                .and()
                .requiresChannel()
                .requestMatchers("/**")
                .requiresSecure()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .deleteCookies("ActiveID")
                .logoutUrl("/logout")
                .and()
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Don't allow the auto registration of the filter for the main request flow. This filter should be limited
     * to the spring security chain.
     *
     * @param filter the Filter instance to disable in the main flow
     * @return the registration bean that designates the filter as being disabled in the main flow
     */
    @Bean
    @DependsOn("blCacheManager")
    public FilterRegistrationBean blCsrfFilterFilterRegistrationBean(@Qualifier("blCsrfFilter") SecurityFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

}
