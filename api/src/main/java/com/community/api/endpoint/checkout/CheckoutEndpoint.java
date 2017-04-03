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

package com.community.api.endpoint.checkout;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.broadleafcommerce.rest.api.wrapper.OrderPaymentWrapper;
import com.broadleafcommerce.rest.api.wrapper.OrderWrapper;
import com.broadleafcommerce.rest.api.wrapper.PaymentTransactionWrapper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * This is a reference REST API endpoint for checkout. This can be modified, used as is, or removed. 
 * The purpose is to provide an out of the box RESTful checkout service implementation, but also 
 * to allow the implementor to have fine control over the actual API, URIs, and general JAX-RS annotations.
 * 
 * @author Kelly Tisdell
 *
 */
@RestController
@RequestMapping(value = "/cart/checkout",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class CheckoutEndpoint extends com.broadleafcommerce.rest.api.endpoint.checkout.CheckoutEndpoint {

    @Override
    @RequestMapping(value = "/payments", method = RequestMethod.GET)
    public List<OrderPaymentWrapper> findPaymentsForOrder(HttpServletRequest request,
                                                          @RequestParam("cartId") Long cartId) {
        return super.findPaymentsForOrder(request, cartId);
    }

    @Override
    @RequestMapping(value = "/payment", method = RequestMethod.POST,
        consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public OrderPaymentWrapper addPaymentToOrder(HttpServletRequest request,
                                                 @RequestBody OrderPaymentWrapper wrapper,
                                                 @RequestParam("cartId") Long cartId) {
        return super.addPaymentToOrder(request, wrapper, cartId);
    }
    
    @Override
    @RequestMapping(value = "/payment/{customerPaymentId}", method = RequestMethod.POST)
    public OrderPaymentWrapper addPaymentToOrderById(HttpServletRequest request,
                                                     @RequestParam("amount") Double amount,
                                                     @RequestParam("currency") String currencyCode,
                                                     @PathVariable("customerPaymentId") Long customerPaymentId,
                                                     @RequestParam("cartId") Long cartId) {
        return super.addPaymentToOrderById(request, amount, currencyCode, customerPaymentId, cartId);
    }
    
    @Override
    @RequestMapping(value = "/payment/{paymentId}/transaction", method = RequestMethod.PUT)
    public OrderPaymentWrapper addOrderPaymentTransaction(HttpServletRequest request, 
                                                          @PathVariable("paymentId") Long orderPaymentId,
                                                          @RequestParam PaymentTransactionWrapper wrapper,
                                                          @RequestParam("cartId") Long cartId) {
        return super.addOrderPaymentTransaction(request, orderPaymentId, wrapper, cartId);
    }
    
    @Override
    @RequestMapping(value = "/payment/{paymentId}", method = RequestMethod.DELETE)
    public OrderWrapper removePaymentFromOrderById(HttpServletRequest request,
                                               @PathVariable("paymentId") Long paymentId,
                                               @RequestParam("cartId") Long cartId) {
        return super.removePaymentFromOrderById(request, paymentId, cartId);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public OrderWrapper performCheckout(HttpServletRequest request,
            @RequestParam("cartId") Long cartId) {
        return super.performCheckout(request, cartId);
    }

}
