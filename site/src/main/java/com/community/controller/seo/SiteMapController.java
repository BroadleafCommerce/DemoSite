/*-
 * #%L
 * Community Demo Site
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
package com.community.controller.seo;

import org.broadleafcommerce.common.sitemap.controller.BroadleafSiteMapController;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Controller to generate and retrieve site map files.
 * 
 * @author Joshua Skorton (jskorton)
 */
@Controller
public class SiteMapController extends BroadleafSiteMapController {
    
    /**
     * Retrieves a site map file
     * 
     * @param request
     * @param response
     * @param model
     * @param fileName
     * @return
     */
    @RequestMapping(value = { "/sitemap*.xml", "sitemap*.gz" })
    @ResponseBody
    public FileSystemResource retrieveSiteMapIndex(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        return super.retrieveSiteMapFile(request, response);
    }
}
