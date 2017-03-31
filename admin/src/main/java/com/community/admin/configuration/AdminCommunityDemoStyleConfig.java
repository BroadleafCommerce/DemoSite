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
