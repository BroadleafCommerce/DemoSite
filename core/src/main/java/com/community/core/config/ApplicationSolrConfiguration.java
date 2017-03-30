/**
 * 
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

    @Value("${oms.search.order.service.solr.primary.location}")
    protected String primaryOrderSolrUrl;

    @Value("${oms.search.order.service.solr.reindex.location}")
    protected String reindexOrderSolrUrl;

    @Value("${oms.search.order.service.solr.admin.location}")
    protected String adminOrderSolrUrl;

    @Value("${oms.search.customer.service.solr.primary.location}")
    protected String primaryCustomerSolrUrl;

    @Value("${oms.search.customer.service.solr.reindex.location}")
    protected String reindexCustomerSolrUrl;

    @Value("${oms.search.customer.service.solr.admin.location}")
    protected String adminCustomerSolrUrl;

    @Value("${oms.search.fulfillmentOrder.service.solr.primary.location}")
    protected String primaryFulfillmentOrderSolrUrl;

    @Value("${oms.search.fulfillmentOrder.service.solr.reindex.location}")
    protected String reindexFulfillmentOrderSolrUrl;

    @Value("${oms.search.fulfillmentOrder.service.solr.admin.location}")
    protected String adminFulfillmentOrderSolrUrl;
    
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
    public SolrClient primaryOrderSolrClient() {
        return new HttpSolrClient(primaryOrderSolrUrl);
    }

    @Bean
    public SolrClient reindexOrderSolrClient() {
        return new HttpSolrClient(reindexOrderSolrUrl);
    }

    @Bean
    public SolrClient adminOrderSolrClient() {
        return new HttpSolrClient(adminOrderSolrUrl);
    }

    @Bean
    public SolrClient primaryCustomerSolrClient() {
        return new HttpSolrClient(primaryCustomerSolrUrl);
    }

    @Bean
    public SolrClient reindexCustomerSolrClient() {
        return new HttpSolrClient(reindexCustomerSolrUrl);
    }

    @Bean
    public SolrClient adminCustomerSolrClient() {
        return new HttpSolrClient(adminCustomerSolrUrl);
    }

    @Bean
    public SolrClient primaryFulfillmentOrderSolrClient() {
        return new HttpSolrClient(primaryFulfillmentOrderSolrUrl);
    }

    @Bean
    public SolrClient reindexFulfillmentOrderSolrClient() {
        return new HttpSolrClient(reindexFulfillmentOrderSolrUrl);
    }

    @Bean
    public SolrClient adminFulfillmentOrderSolrClient() {
        return new HttpSolrClient(adminFulfillmentOrderSolrUrl);
    }

    @Bean
    public SolrConfiguration blCatalogSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(primaryCatalogSolrClient(), reindexCatalogSolrClient(), adminCatalogSolrClient());
    }

    @Bean
    public SolrConfiguration blOrderSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(primaryOrderSolrClient(), reindexOrderSolrClient(), adminOrderSolrClient());
    }

    @Bean
    public SolrConfiguration blCustomerSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(primaryCustomerSolrClient(), reindexCustomerSolrClient(), adminCustomerSolrClient());
    }

    @Bean
    public SolrConfiguration blFulfillmentOrderSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(primaryFulfillmentOrderSolrClient(), reindexFulfillmentOrderSolrClient(), adminFulfillmentOrderSolrClient());
    }
    
    @Bean
    protected SearchService blSearchService() {
        return new SolrSearchServiceImpl();
    }
    
}
