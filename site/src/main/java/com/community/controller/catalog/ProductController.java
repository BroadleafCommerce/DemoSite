/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
