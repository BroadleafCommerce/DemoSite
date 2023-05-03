/*-
 * #%L
 * Private Demo Admin
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
package com.community.admin.web.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Creates a nonce token for use with Content Security Policy script blocks.
 * 
 * Expects a Content Security Policy with a single string placeholder (%s).  The created nonce will be 
 * - added to a request attribute ("blcCspNonce")
 * - for use in a nonce varible on script tags
 * 
 * <code>
 *     <script th:attr="nonce=${blcCspNonce}">
 * </code>
 *
 * This filter would typically be initialized in the AdminSecurityConfig as a add filter.
 * 
 * <code>
 * .   addFilterAfter(new AdminContentSecurityPolicyfFilter(cspHeaderValue), AdminSecurityFilter.class)
 * </code>
 * 
 * Note that multiple http requests can be made each of which would traverse the filter pipeline and
 * each would receive a unique token.  This should not be a problem as the response header will coincide
 * with the nonce provided in the HTML script/tag.  If it is desired to skip token generation for a specific
 * request, the excludeRequestPattern could be used.
 * 
 * @author Daniel Colgrove
 */
public class AdminContentSecurityPolicyFilter extends OncePerRequestFilter {

    protected static final Log LOG = LogFactory.getLog(AdminContentSecurityPolicyFilter.class);

    protected List<String> excludedRequestPatterns;
    protected final String contentSecurityPolicy;

    public AdminContentSecurityPolicyFilter(String csp) {
        this.contentSecurityPolicy = csp;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean excludedRequestFound = false;
        if (excludedRequestPatterns != null && excludedRequestPatterns.size() > 0) {
            for (String pattern : excludedRequestPatterns) {
                RequestMatcher matcher = new AntPathRequestMatcher(pattern);
                if (matcher.matches(request)) {
                    excludedRequestFound = true;
                    break;
                }
            }
        }

        if (!excludedRequestFound) {
            establishNonceToken(request, response);
        }
        
        filterChain.doFilter(request, response);
    }

    protected void establishNonceToken(HttpServletRequest request, HttpServletResponse response) {
        final String nonce = UUID.randomUUID().toString();
        request.setAttribute("blcCspNonce", nonce);
        
        LOG.debug(String.format("Nonce token set to %s", nonce));
        
        String headerValue = contentSecurityPolicy.replaceAll("%s", nonce);
        response.addHeader("Content-Security-Policy", headerValue);
    }
    
    public List<String> getExcludedRequestPatterns() {
        return excludedRequestPatterns;
    }

    /**
     * This allows you to declaratively set a list of excluded Request Patterns
     *
     * <bean id="blFilter" class="org.broadleafcommerce.common.security.handler.CsrfFilter" >
     *     <property name="excludedRequestPatterns">
     *         <list>
     *             <value>/exclude-me/**</value>
     *         </list>
     *     </property>
     * </bean>
     *
     **/
    public void setExcludedRequestPatterns(List<String> excludedRequestPatterns) {
        this.excludedRequestPatterns = excludedRequestPatterns;
    }

}

