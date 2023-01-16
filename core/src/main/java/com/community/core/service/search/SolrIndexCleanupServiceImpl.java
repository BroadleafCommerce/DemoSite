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
package com.community.core.service.search;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.event.ReindexEvent;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;


/**
 * This class holds methods that are used to clean up parts of the Solr index specifically associated with the private 
 * demo functionality.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Service("pdSolrIndexCleanupService")
public class SolrIndexCleanupServiceImpl implements SolrIndexCleanupService {
    protected static final Log LOG = LogFactory.getLog(SolrIndexCleanupServiceImpl.class);

    protected static final String DDL_PROP = "blPU.hibernate.hbm2ddl.auto";
    protected static final String[] QUAL_VALUES = new String[] { "create", "create-drop" };

    @Autowired(required = false)
    protected SolrIndexService sis;
    protected final ApplicationEventPublisher publisher;
    protected final SystemPropertiesService propService;

    @Autowired
    public SolrIndexCleanupServiceImpl(ApplicationEventPublisher publisher, SystemPropertiesService propService) {
        this.publisher = publisher;
        this.propService = propService;
    }

    @Override
    @Transactional(value = "blTransactionManager", readOnly = true)
    @EventListener(ContextRefreshedEvent.class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    public void rebuildIndexAtStartupIfNecessary() throws ServiceException, IOException {
        if (sis != null) {
            String propVal = propService.resolveSystemProperty(DDL_PROP).toLowerCase();
            if (ArrayUtils.contains(QUAL_VALUES, propVal)) {
                sis.rebuildIndex();
                publisher.publishEvent(new ReindexEvent(this));
                LOG.info("All indexes rebuilt at startup because value was " + propVal);
            } else {
                LOG.info("Not rebuilding indexes because value was " + propVal);
            }
        }
    }

}
