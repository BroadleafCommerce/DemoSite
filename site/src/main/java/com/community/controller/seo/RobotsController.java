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

import org.broadleafcommerce.cms.web.controller.BroadleafRobotsController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Controller to retrieve robots.txt file.   
 * 
 * @author bpolster
 * @see BroadleafRobotsController
 */
@Controller
public class RobotsController extends BroadleafRobotsController {

    /**
     * Retrieves the robots.txt file     
     *  
     * @param request
     * @param response
     * @param model
     * @param fileName
     * @return
     */
    @RequestMapping(value = { "/robots.txt" })
    @ResponseBody
    public String getRobotsFile(HttpServletRequest request, HttpServletResponse response) {
        return super.getRobotsFile(request, response);
    }
}
