package com.community.core.config;

import org.broadleafcommerce.common.demo.ImportCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration("blCommunityDemoData")
@Conditional(ImportCondition.class)
public class ImportSQLConfig {}