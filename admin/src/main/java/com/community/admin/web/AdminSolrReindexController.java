/*-
 * #%L
 * Community Demo Admin
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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
package com.community.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexService;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.service.navigation.AdminNavigationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.Resource;

@Controller("blAdminCommunitySolrIndexerController")
public class AdminSolrReindexController extends BroadleafAbstractController {

    private static final Log LOG = LogFactory.getLog(AdminSolrReindexController.class);

    @Resource(name = "blAdminNavigationService")
    protected AdminNavigationService adminNavigationService;

    @Resource(name = "blSolrIndexService")
    protected SolrIndexService solrIndexService;

    @RequestMapping(
            value = {"/indexer"},
            method = {RequestMethod.GET}
    )
    public String viewIndexerPage(Model model) throws Exception {
        model.addAttribute("customView", "/community_indexer.html");
        this.setModelAttributes(model, "/indexer");
        return "modules/emptyContainer";
    }

    @RequestMapping(
            value = {"/indexer"},
            method = {RequestMethod.POST}
    )
    public String triggerReindex(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("reindexType", "triggered");
        new Thread(()-> {
            try {
                solrIndexService.rebuildIndex();
            } catch (Exception ex){
                LOG.error(ex);
            }
        }).start();
        return "redirect:/indexer";
    }

    protected void setModelAttributes(Model model, String sectionKey) {
        AdminSection section = this.adminNavigationService.findAdminSectionByURI(sectionKey);
        if (section != null) {
            model.addAttribute("sectionKey", sectionKey);
            model.addAttribute("currentAdminModule", section.getModule());
            model.addAttribute("currentAdminSection", section);
        }

    }

}
