package com.community.core.config;

import org.broadleafcommerce.common.demo.AutoImportPersistenceUnit;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jeff Fischer
 */
@Configuration
@ComponentScan("com.community.core")
public class CoreConfig {
    @Bean
    public AutoImportSql blCommunitySolrIndexerData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"/sql/load_solr_reindex_community.sql", 9999);
    }

}
