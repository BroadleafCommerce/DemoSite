package com.community.core.config;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.RequestWriter;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.search.service.solr.SolrConfiguration;
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Optional.empty;

@Component
@ConditionalOnProperty(name = "using.solr.cloud", havingValue = "true")
public class ApplicationSolrCloudConfiguration {

    @Value("#{'${solr.url.primary}'.split(',')}")
    protected List<String> primarySolrUrl;

    @Value("#{'${solr.url.reindex}'.split(',')}")
    protected List<String> reindexSolrUrl;

    @Value("${solr.cloud.defaultNumShards}")
    protected int defaultNumShards;

    @Value("${solr.cloud.defaultNumReplicas}")
    protected int defaultNumReplicas;

    @Value("${solr.cloud.configName}")
    protected String configName;

    @Bean
    public SolrConfiguration blCatalogSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(
                this.primaryCatalogSolrCloudClient(),
                this.reindexCatalogSolrCloudClient(),
                this.configName,
                this.defaultNumShards,
                this.defaultNumReplicas
        );
    }

    @Bean
    public SolrConfiguration blOrderSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(
                this.primaryOrderSolrCloudClient(),
                this.reindexOrderSolrCloudClient(),
                this.configName,
                this.defaultNumShards,
                this.defaultNumReplicas
        );
    }

    @Bean
    public SolrConfiguration blCustomerSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(
                this.primaryCustomerSolrCloudClient(),
                this.reindexCustomerSolrCloudClient(),
                this.configName,
                this.defaultNumShards,
                this.defaultNumReplicas
        );
    }

    @Bean
    public SolrConfiguration blFulfillmentOrderSolrConfiguration() throws IllegalStateException {
        return new SolrConfiguration(
                this.primaryFulfillmentOrderSolrCloudClient(),
                this.reindexFulfillmentOrderSolrCloudClient(),
                this.configName,
                this.defaultNumShards,
                this.defaultNumReplicas
        );
    }

    @Bean
    public CloudSolrClient primaryCatalogSolrCloudClient() {
        return new CloudSolrClient.Builder(primarySolrUrl, empty())
                .withDefaultCollection("catalogs")
                .withRequestWriter(new RequestWriter())
                .build();
    }

    @Bean
    public CloudSolrClient reindexCatalogSolrCloudClient() {
        return new CloudSolrClient.Builder(reindexSolrUrl, empty())
                .withDefaultCollection("catalogs_reindex")
                .withRequestWriter(new RequestWriter())
                .build();
    }

    @Bean
    public CloudSolrClient primaryOrderSolrCloudClient() {
        return new CloudSolrClient.Builder(primarySolrUrl, empty())
                .withDefaultCollection("orders")
                .withRequestWriter(new RequestWriter())
                .build();
    }

    @Bean
    public CloudSolrClient reindexOrderSolrCloudClient() {
        return new CloudSolrClient.Builder(primarySolrUrl, empty())
                .withDefaultCollection("orders_reindex")
                .withRequestWriter(new RequestWriter())
                .build();
    }

    @Bean
    public CloudSolrClient primaryCustomerSolrCloudClient() {
        return new CloudSolrClient.Builder(primarySolrUrl, empty())
                .withDefaultCollection("customers")
                .withRequestWriter(new RequestWriter())
                .build();
    }

    @Bean
    public CloudSolrClient reindexCustomerSolrCloudClient() {
        return new CloudSolrClient.Builder(primarySolrUrl, empty())
                .withDefaultCollection("customers_reindex")
                .withRequestWriter(new RequestWriter())
                .build();
    }

    @Bean
    public CloudSolrClient primaryFulfillmentOrderSolrCloudClient() {
        return new CloudSolrClient.Builder(primarySolrUrl, empty())
                .withDefaultCollection("fulfillment_orders")
                .withRequestWriter(new RequestWriter())
                .build();
    }

    @Bean
    public CloudSolrClient reindexFulfillmentOrderSolrCloudClient() {
        return new CloudSolrClient.Builder(primarySolrUrl, empty())
                .withDefaultCollection("fulfillment_orders_reindex")
                .withRequestWriter(new RequestWriter())
                .build();
    }

    @Bean
    protected SearchService blSearchService() {
        return new SolrSearchServiceImpl();
    }

}