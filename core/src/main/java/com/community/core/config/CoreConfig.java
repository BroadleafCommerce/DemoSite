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
