/*-
 * #%L
 * Community Demo Admin
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
package com.community.admin.configuration;

import com.community.admin.web.filter.AdminContentSecurityPolicyFilter;
import org.broadleafcommerce.common.security.handler.SecurityFilter;
import org.broadleafcommerce.openadmin.security.BroadleafAdminAuthenticationFailureHandler;
import org.broadleafcommerce.openadmin.security.BroadleafAdminAuthenticationSuccessHandler;
import org.broadleafcommerce.openadmin.web.filter.AdminSecurityFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.Filter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@EnableWebSecurity
@ComponentScan({"org.broadleafcommerce.profile.web.core.security", "org.broadleafcommerce.core.web.order.security"})
@EnableMethodSecurity(securedEnabled = true)
public class AdminSecurityConfig {

    @Configuration
    public static class DependencyConfiguration {

        @Bean
        protected AuthenticationFailureHandler blAdminAuthenticationFailureHandler() {
            return new BroadleafAdminAuthenticationFailureHandler("/login?login_error=true");
        }

        @Bean
        protected AuthenticationSuccessHandler blAdminAuthenticationSuccessHandler() {
            BroadleafAdminAuthenticationSuccessHandler handler = new BroadleafAdminAuthenticationSuccessHandler();
            handler.setDefaultTargetUrl("/loginSuccess");
            handler.setAlwaysUseDefaultTargetUrl(false);
            return handler;
        }

    }

    @Value("${http.server.port:8081}")
    protected int httpServerPort;

    @Value("${server.port:8444}")
    protected int httpsRedirectPort;

    @Value("${site.baseurl.domain}")
    private String baseUrl;

    @Value("${site.baseurl.port}")
    private String baseUrlPort;


    @Value("${asset.server.url.prefix.internal}")
    protected String assetServerUrlPrefixInternal;

    @Value("${enforce.secure:true}")
    protected boolean enforceSecure;

    @Resource(name = "blAdminAuthenticationSuccessHandler")
    protected AuthenticationSuccessHandler successHandler;

    @Resource(name = "blAdminAuthenticationFailureHandler")
    protected AuthenticationFailureHandler failureHandler;

    @Resource(name = "blAdminLogoutSuccessHandler")
    protected LogoutSuccessHandler logoutSuccessHandler;

    @Resource(name = "blAdminCsrfFilter")
    protected Filter adminCsrfFilter;

    @Resource(name = "blAdminUserDetailsService")
    protected UserDetailsService adminUserDetailsService;

    @Resource(name = "blAdminPasswordEncoder")
    protected PasswordEncoder passwordEncoder;

    @Resource(name = "blAdminAuthenticationProvider")
    protected AuthenticationProvider authenticationProvider;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                antMatcher("/**/*.css"),
                antMatcher("/**/*.js"),
                antMatcher("/img/**"),
                antMatcher("/fonts/**"),
                antMatcher("/**/" + assetServerUrlPrefixInternal + "/**"),
                antMatcher("/favicon.ico"),
                antMatcher("/robots.txt")
        );
    }

    @Bean(name = "blAdminAuthenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .csrf().disable()
                .headers().frameOptions().disable().and()
                .sessionManagement()
                .enableSessionUrlRewriting(false)
                .and()
                .formLogin()
                .permitAll()
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .loginPage("/login")
                .loginProcessingUrl("/login_admin_post")
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/sendResetPassword", "/forgotUsername", "/forgotPassword", "/resetPassword", "/login")
                .permitAll()
                .requestMatchers("/**")
                .authenticated()
                .and()
                .requiresChannel()
                .requestMatchers("/**")
                .requiresSecure()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .logoutUrl("/adminLogout.htm")
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .portMapper()
                .http(80).mapsTo(443)
                .http(8080).mapsTo(8443)
                .http(httpServerPort).mapsTo(httpsRedirectPort)
                .and()
                .addFilterBefore(adminCsrfFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new AdminContentSecurityPolicyFilter(getCSPHeader()), AdminSecurityFilter.class);
        return http.build();
    }

    /**
     * Add the site's baseUrl and port as a frame-src value to the Content Security Policy header.
     * This enables admin's CSR mode to load the site in a modal.
     * @return The CSP header
     */
    private String getCSPHeader() {
        // Can contain one or more %s placeholders for substitution of a nonce token (e.g. 'nonce-%s')
        return "default-src 'self';script-src 'self' 'unsafe-eval' 'nonce-%s'; " +
                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; img-src 'self' data:; " +
                "font-src 'self' https://fonts.gstatic.com data:; frame-src 'self' *." + baseUrl + ":" + baseUrlPort + ";";
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
    public FilterRegistrationBean blAdminCsrfFilterFilterRegistrationBean(@Qualifier("blAdminCsrfFilter") SecurityFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

}
