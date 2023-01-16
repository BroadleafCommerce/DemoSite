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

import org.apache.catalina.connector.Connector;
import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexService;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.community.core.config.CoreConfig;
import com.community.core.config.StringFactoryBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@Import({
        CoreConfig.class,
        SiteSecurityConfig.class})
public class SiteConfig {

    @Bean
    @ConditionalOnProperty("jmx.app.name")
    public StringFactoryBean blJmxNamingBean() {
        return new StringFactoryBean();
    }

    @Merge("blMessageSourceBaseNames")
    public List<String> customMessages() {
        return Arrays.asList("classpath:messages");
    }

    /**
     * Disables caching only in the 'default' profile which is useful for development.
     */
    @Merge("blMergedCacheConfigLocations")
    @Profile("default")
    public List<String> removeCachingConfiguration() {
        return Arrays.asList("classpath:bl-override-ehcache.xml");
    }

    /**
     * Broadleaf Commerce comes with an Image Server that allows you to manipulate images.   For example, the
     *  demo includes a high resolution image for each product that is reduced in size for browsing operations
     */
    @Merge("blStaticMapNamedOperations")
    public Map<String, Map<String, String>> customImageOperations() {
        Map<String, Map<String, String>> operations = new HashMap<>();
        Map<String, String> browseOperation = new HashMap<>();
        browseOperation.put("resize-width-amount", "400");
        browseOperation.put("resize-height-amount", "400");
        browseOperation.put("resize-high-quality", "false");
        browseOperation.put("resize-maintain-aspect-ratio", "true");
        browseOperation.put("resize-reduce-only", "true");
        operations.put("browse", browseOperation);

        Map<String, String> thumbnailOperation = new HashMap<>();
        thumbnailOperation.put("resize-width-amount", "60");
        thumbnailOperation.put("resize-height-amount", "60");
        thumbnailOperation.put("resize-high-quality", "false");
        thumbnailOperation.put("resize-maintain-aspect-ratio", "true");
        thumbnailOperation.put("resize-reduce-only", "true");
        operations.put("thumbnail", thumbnailOperation);

        return operations;
    }

    /**
     * This ensures the Solr index is rebuilt at a regular interval since there is no automatic rebuilding or
     * invalidation of the index in core Broadleaf otherwise
     *
     * @author Phillip Verheyden (phillipuniverse)
     */
    @Configuration
    public static class SolrReindexConfig {

        @Bean
        public SchedulerFactoryBean rebuildIndexScheduler(@Qualifier("rebuildIndexTrigger") Trigger rebuildIndexTrigger) {
            SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
            scheduler.setTriggers(rebuildIndexTrigger);
            return scheduler;
        }

        @Bean
        public SimpleTriggerFactoryBean rebuildIndexTrigger(@Qualifier("solrReindexJobDetail") JobDetail detail,
            @Value("${solr.index.start.delay}") long startDelay,
            @Value("${solr.index.repeat.interval}") long repeatInterval) {
            SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
            trigger.setJobDetail(detail);
            trigger.setStartDelay(startDelay);
            trigger.setRepeatInterval(repeatInterval);
            return trigger;
        }

        @Bean
        public FactoryBean<JobDetail> solrReindexJobDetail(SolrIndexService indexService) {
            MethodInvokingJobDetailFactoryBean detail = new MethodInvokingJobDetailFactoryBean();
            detail.setTargetObject(indexService);
            detail.setTargetMethod("rebuildIndex");
            return detail;
        }
    }

    /**
     * Spring Boot does not support the configuration of both an HTTP connector and an HTTPS connector via properties.
     * In order to have both, we’ll need to configure one of them programmatically (HTTP).
     * Below is the recommended approach according to the Spring docs:
     * {@link https://github.com/spring-projects/spring-boot/blob/1.5.x/spring-boot-docs/src/main/asciidoc/howto.adoc#configure-ssl}
     * @param httpServerPort
     * @return EmbeddedServletContainerFactory
     */
    @Bean
    public TomcatServletWebServerFactory tomcatEmbeddedServletContainerFactory(@Value("${http.server.port:8080}") int httpServerPort) {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector(httpServerPort));
        return tomcat;
    }

    private Connector createStandardConnector(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        return connector;
    }

}
