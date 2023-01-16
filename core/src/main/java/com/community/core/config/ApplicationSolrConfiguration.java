/*-
 * #%L
 * Community Demo Core
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
        return new HttpSolrClient.Builder(primaryCatalogSolrUrl).build();
    }
    
    @Bean
    public SolrClient reindexCatalogSolrClient() {
        return new HttpSolrClient.Builder(reindexCatalogSolrUrl).build();
    }
    
    @Bean
    public SolrClient adminCatalogSolrClient() {
        return new HttpSolrClient.Builder(adminCatalogSolrUrl).build();
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
