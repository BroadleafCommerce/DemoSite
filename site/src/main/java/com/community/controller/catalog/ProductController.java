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

import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.web.catalog.ProductHandlerMapping;
import org.broadleafcommerce.core.web.controller.catalog.BroadleafProductController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class works in combination with the ProductHandlerMapping which finds a product based upon
 * the passed in URL.
 */
@Controller("blProductController")
public class ProductController extends BroadleafProductController {

    protected static final String DEFAULT_PRODUCT_QUICKVIEW_PATH = "catalog/partials/productQuickView";
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Resource(name = "blSystemPropertiesService")
    protected SystemPropertiesService systemPropertiesService;
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.handleRequest(request, response);
    }

    @RequestMapping(value = "/product-quick-view", params = {"id"})
    public ModelAndView getProductQuickView(final HttpServletRequest request, final HttpServletResponse response, 
                                            @RequestParam("id") final Long productId) throws Exception {
        final Product product = catalogService.findProductById(productId);

        request.setAttribute(ProductHandlerMapping.CURRENT_PRODUCT_ATTRIBUTE_NAME, product);
        
        final ModelAndView modelAndView = super.handleRequest(request, response);
        
        modelAndView.setViewName(getProductQuickViewTemplatePath());

        return modelAndView;
    }

    protected String getProductQuickViewTemplatePath() {
        return systemPropertiesService.resolveSystemProperty("product.quickView.path", DEFAULT_PRODUCT_QUICKVIEW_PATH);
    }
}
