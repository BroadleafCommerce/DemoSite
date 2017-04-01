package com.community.core.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component
public class ApplicationSolrConfiguration {

    @Value("${solr.url.primary}")
    protected String primaryCatalogSolrUrl;
    
    @Value("${solr.url.reindex}")
    protected String reindexCatalogSolrUrl;
    
    @Value("${solr.url.admin}")
    protected String adminCatalogSolrUrl;

    @Bean
    public SolrClient primaryCatalogSolrClient() {
        return new HttpSolrClient(primaryCatalogSolrUrl);
    }
    
    @Bean
    public SolrClient reindexCatalogSolrClient() {
        return new HttpSolrClient(reindexCatalogSolrUrl);
    }
    
    @Bean
    public SolrClient adminCatalogSolrClient() {
        return new HttpSolrClient(adminCatalogSolrUrl);
    }

    @Bean
    public SolrConfiguration blCatalogSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(primaryCatalogSolrClient(), reindexCatalogSolrClient(), adminCatalogSolrClient());
    }

    @Bean
    protected SearchService blSearchService() {
        return new SolrSearchServiceImpl();
    }
    
}
