package com.community.api.filter;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * The purpose of this filter is to be the last one during "response chain" and remove BRC
 * as somehow during some operations, especially during redirects, BRC has stale reference to
 * HttpServletRequest/Response and can fail in random places where logic access request from BRC
 */
@Component
@Order(Integer.MIN_VALUE)
public class CleanBrcFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
        BroadleafRequestContext.setBroadleafRequestContext(null);
    }
}
