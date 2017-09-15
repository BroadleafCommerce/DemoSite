package com.community.core.config;

import org.broadleafcommerce.common.demo.AutoImportPersistenceUnit;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.broadleafcommerce.common.demo.AutoImportStage;
import org.broadleafcommerce.common.demo.ImportCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(ImportCondition.class)
public class DemoSiteImportSqlConfig {
    
    @Bean
    public AutoImportSql contentDataImport() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"sql/fix_content_page_template_ref.sql", AutoImportStage.PRIMARY_LATE);
    }
}