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

package com.community.controller.catalog;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.web.controller.catalog.BroadleafSearchController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/search")
public class SearchController extends BroadleafSearchController {

    @Override
    @RequestMapping("")
    public String search(Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "q") String q) throws ServletException, IOException, ServiceException {
        String returnPath = super.search(model, request,response, q);
        if (isAjaxRequest(request)) {
            returnPath += " :: ajax";
        }
        return returnPath;
    }

}
