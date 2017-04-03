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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.broadleafcommerce.rest.api.wrapper.OrderWrapper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * This is a reference REST API endpoint for order history. This can be modified, used as is, or removed. 
 * The purpose is to provide an out of the box RESTful order history service implementation, but also 
 * to allow the implementor to have fine control over the actual API, URIs, and general JAX-RS annotations.
 * 
 * @author Kelly Tisdell
 *
 */
@RestController
@RequestMapping(value = "/orders",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class OrderHistoryEndpoint extends com.broadleafcommerce.rest.api.endpoint.order.OrderHistoryEndpoint {

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public List<OrderWrapper> findOrdersForCustomer(HttpServletRequest request,
                                                    @RequestParam(value = "orderStatus", defaultValue = "SUBMITTED") String orderStatus) {
        return super.findOrdersForCustomer(request, orderStatus);
    }
    
    @Override
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public List<OrderWrapper> findAllOrdersForCustomer(HttpServletRequest request) {
        return super.findAllOrdersForCustomer(request);
    }
    
    @Override
    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    public OrderWrapper findOrderById(HttpServletRequest request, @PathVariable("orderId") Long orderId) {
        return super.findOrderById(request, orderId);
    }

    @Override
    @RequestMapping(value = "/{orderNumber}", method = RequestMethod.GET)
    public OrderWrapper findOrderByOrderNumber(HttpServletRequest request, @PathVariable String orderNumber) {
        OrderWrapper order = super.findOrderByOrderNumber(request, orderNumber);
        return order;
    }
    
}
