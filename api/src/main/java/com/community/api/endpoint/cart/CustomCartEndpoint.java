/*-
 * #%L
 * Community Demo API
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

package com.community.api.endpoint.cart;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.broadleafcommerce.rest.api.endpoint.order.CartEndpoint;
import com.broadleafcommerce.rest.api.wrapper.OrderWrapper;

import javax.servlet.http.HttpServletRequest;


/**
 * This is a reference REST API endpoint for cart. This can be modified, used as is, or removed. 
 * The purpose is to provide an out of the box RESTful cart service implementation, but also 
 * to allow the implementor to have fine control over the actual API, URIs, and general JAX-RS annotations.
 * 
 * @author Kelly Tisdell
 *
 */
@RestController
@RequestMapping(value = "/cart",
                produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
public class CustomCartEndpoint extends CartEndpoint {

    @Override
    @RequestMapping(value = "", method = RequestMethod.GET)
    public OrderWrapper findCartForCustomer(HttpServletRequest request) {
        try {
            return super.findCartForCustomer(request);
        } catch (Exception e) {
            // if we failed to find the cart, create a new one
            return createNewCartForCustomer(request);
        }
    }
}
