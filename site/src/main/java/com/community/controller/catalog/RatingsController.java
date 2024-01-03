/*-
 * #%L
 * Community Demo Site
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

package com.community.controller.catalog;

import org.broadleafcommerce.core.web.controller.catalog.BroadleafRatingsController;
import org.broadleafcommerce.core.web.controller.catalog.ReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RatingsController extends BroadleafRatingsController {

    @RequestMapping(value = "/reviews/product/{itemId}", method = RequestMethod.GET)
    public String viewReviewForm(HttpServletRequest request, Model model, @PathVariable("itemId") String itemId, @ModelAttribute("reviewForm") ReviewForm form) {
        return super.viewReviewForm(request, model, form, itemId);
    }
    
    @RequestMapping(value = "/reviews/product/{itemId}", method = RequestMethod.POST)
    public String reviewItem(HttpServletRequest request, Model model, @PathVariable("itemId") String itemId, @ModelAttribute("reviewForm") ReviewForm form) {
        return super.reviewItem(request, model, form, itemId);
    }
    
    
}
