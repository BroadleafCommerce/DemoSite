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

package com.community.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafRedirectController;
import org.broadleafcommerce.common.web.security.BroadleafAuthenticationSuccessRedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The controller expects a session attribute to be set called BLC_REDIRECT_URL and 
 * that it is being called from an Ajax redirect process.
 * 
 * It would be unexpected for an implementor to modify this class or the corresponding view
 * blcRedirect.html.  
 * 
 * The purpose of this class is to support ajax redirects after a successful login. 
 * 
 * @see BroadleafAuthenticationSuccessRedirectStrategy
 * 
 * @author bpolster
 */
@Controller
public class RedirectController extends BroadleafRedirectController {
    
    @RequestMapping("/redirect")
    public String redirect(HttpServletRequest request, HttpServletResponse response, Model model) {
        return super.redirect(request, response, model);
    }
}
