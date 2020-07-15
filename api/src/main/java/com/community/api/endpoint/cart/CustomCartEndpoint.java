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
