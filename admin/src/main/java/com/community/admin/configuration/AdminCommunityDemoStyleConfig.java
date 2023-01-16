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

import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.resolver.BroadleafClasspathTemplateResolver;
import org.broadleafcommerce.presentation.resolver.BroadleafTemplateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@ConditionalOnTemplating
public class AdminCommunityDemoStyleConfig {

    @Bean
    public BroadleafClasspathTemplateResolver adminPrivateDemoStyleTemplateResolver(@Value("${cache.page.templates}") boolean cachePageTemplates,
                                                                              @Value("${cache.page.templates.ttl}") long cacheTTL) {
        BroadleafClasspathTemplateResolver templateResolver = new BroadleafClasspathTemplateResolver();
        templateResolver.setPrefix("community-demo-style/templates/admin/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(BroadleafTemplateMode.HTML);
        templateResolver.setCacheable(cachePageTemplates);
        templateResolver.setCacheTTLMs(cacheTTL);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(100);
        return templateResolver;
    }

    @Merge("blJsLocations")
    public List<String> adminPrivateDemoStyleJsLocations() {
        return Collections.singletonList("classpath:/community-demo-style/js/");
    }

    @Merge("blCssLocations")
    public List<String> adminPrivateDemoStyleCssLocations() {
        return Collections.singletonList("classpath:/community-demo-style/css/");
    }

}
