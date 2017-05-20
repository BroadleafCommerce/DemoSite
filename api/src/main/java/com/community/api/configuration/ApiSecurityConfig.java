package com.community.api.configuration;

import org.broadleafcommerce.profile.web.core.security.RestApiCustomerStateFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.channel.ChannelDecisionManagerImpl;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

import javax.servlet.Filter;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${asset.server.url.prefix.internal}")
    protected String assetServerUrlPrefixInternal;

    @Bean(name="blAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("broadleafapi")
            .password("changeit")
            .roles("USER");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .antMatcher("/api/**")
            .httpBasic()
            .and()
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/**/webjars/**",
                    "/api/**/images/favicon-*",
                    "/api/**/jhawtcode/**",
                    "/api/**/swagger-ui.html")
                .anonymous()
                .and()
            .sessionManagement()
                .sessionFixation()
                .none()
                .enableSessionUrlRewriting(false)
                .and()
            .requiresChannel()
                .anyRequest()
                .requires(ChannelDecisionManagerImpl.ANY_CHANNEL)
            .and()
            .addFilterAfter(apiCustomerStateFilter(), RememberMeAuthenticationFilter.class);    }
    
    @Bean
    public Filter apiCustomerStateFilter() {
        return new RestApiCustomerStateFilter();
    }
    
}
