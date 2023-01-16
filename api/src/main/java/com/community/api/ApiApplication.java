/*-
 * #%L
 * Community Demo API
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
package com.community.api;

import org.broadleafcommerce.common.config.EnableBroadleafSiteRootAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@SpringBootApplication
@EnableAutoConfiguration
public class ApiApplication {

    @Configuration
    @EnableBroadleafSiteRootAutoConfiguration
    public static class BroadleafFrameworkConfiguration {}

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
    
}
