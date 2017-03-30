package com.community.configuration;

import org.apache.catalina.connector.Connector;
import org.broadleafcommerce.common.extensibility.context.merge.Merge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

import com.community.core.config.CoreConfig;
import com.community.core.config.StringFactoryBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Configuration
@Import({
        CoreConfig.class,
        SiteSecurityConfig.class})
@ImportResource({
        "classpath:applicationContext.xml",
})
public class SiteConfig {

    @Bean
    @ConditionalOnProperty("jmx.app.name")
    public StringFactoryBean blJmxNamingBean() {
        return new StringFactoryBean();
    }

    /**
     * Initialize Broadleaf's Servlet Context
     * @return ServletContextInitializer
     */
    @Bean
    public ServletContextInitializer initialize() {
        return new SiteServletContextInitializer();
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
    
    @Merge("blWebDialects")
    public Set<?> siteWebDialect() {
        return Collections.singleton(new LayoutDialect());
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
    public EmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory(@Value("${http.server.port:8080}") int httpServerPort) {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector(httpServerPort));
        return tomcat;
    }

    private Connector createStandardConnector(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        return connector;
    }

}
