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

import org.broadleafcommerce.core.web.controller.account.BroadleafOrderHistoryController;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/account/orders")
public class OrderHistoryController extends BroadleafOrderHistoryController {

    @Resource(name = "blStateService")
    StateService stateService;

    @Resource(name = "blCountryService")
    CountryService countryService;

    @RequestMapping(method = RequestMethod.GET)
    public String viewOrderHistory(HttpServletRequest request, Model model) {
        return super.viewOrderHistory(request, model); 
    }

    @RequestMapping(value = "/{orderNumber}", method = RequestMethod.GET)
    public String viewOrderDetails(HttpServletRequest request, Model model, @PathVariable("orderNumber") String orderNumber) {
        return super.viewOrderDetails(request, model, orderNumber);
    }

}
