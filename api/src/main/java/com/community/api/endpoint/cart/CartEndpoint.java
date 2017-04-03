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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.broadleafcommerce.rest.api.wrapper.OrderAttributeWrapper;
import com.broadleafcommerce.rest.api.wrapper.OrderItemAttributeWrapper;
import com.broadleafcommerce.rest.api.wrapper.OrderItemWrapper;
import com.broadleafcommerce.rest.api.wrapper.OrderWrapper;

import java.util.List;

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
public class CartEndpoint extends com.broadleafcommerce.rest.api.endpoint.order.CartEndpoint {

    
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

    @Override
    @RequestMapping(value = "", method = RequestMethod.POST)
    public OrderWrapper createNewCartForCustomer(HttpServletRequest request) {
        return super.createNewCartForCustomer(request);
    }

    @Override
    @RequestMapping(value = "/{cartId}", method = RequestMethod.GET)
    public OrderWrapper findCartById(HttpServletRequest request,
                                     @PathVariable("cartId") Long cartId) {
        return super.findCartById(request, cartId);
    }

    @RequestMapping(value = "/{cartId}/item", method = RequestMethod.POST)
    public OrderWrapper addItemToOrder(HttpServletRequest request,
                                          @PathVariable("cartId") Long cartId,
                                          @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder,
                                          @RequestBody OrderItemWrapper orderItemWrapper) {
        return super.addItemToOrder(request, cartId, orderItemWrapper, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/items/{itemId}", method = RequestMethod.DELETE)
    public OrderWrapper removeItemFromOrder(HttpServletRequest request,
                                            @PathVariable("itemId") Long itemId,
                                            @PathVariable("cartId") Long cartId,
                                            @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder) {
        return super.removeItemFromOrder(request, itemId, cartId, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/items/{itemId}", method = RequestMethod.PUT)
    public OrderWrapper updateItemQuantity(HttpServletRequest request,
                                           @PathVariable("itemId") Long itemId,
                                           @PathVariable("cartId") Long cartId,
                                           @RequestParam("quantity") Integer quantity,
                                           @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder) {
        return super.updateItemQuantity(request, itemId, cartId, quantity, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/offer/{promoCode}", method = RequestMethod.POST)
    public OrderWrapper addOfferCode(HttpServletRequest request,
                                     @PathVariable("promoCode") String promoCode,
                                     @PathVariable("cartId") Long cartId,
                                     @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder) {
        return super.addOfferCode(request, promoCode, cartId, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/offer/{promoCode}", method = RequestMethod.DELETE)
    public OrderWrapper removeOfferCode(HttpServletRequest request,
                                        @PathVariable("promoCode") String promoCode,
                                        @PathVariable("cartId") Long cartId,
                                        @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder) {
        return super.removeOfferCode(request, promoCode, cartId, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/offers", method = RequestMethod.DELETE)
    public OrderWrapper removeAllOfferCodes(HttpServletRequest request,
                                            @PathVariable("cartId") Long cartId,
                                            @RequestParam(value = "priceOrder", required = false, defaultValue = "true") boolean priceOrder) {
        return super.removeAllOfferCodes(request, cartId, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/attributes", method = RequestMethod.PUT,
        consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public OrderWrapper updateOrderAttributes(HttpServletRequest request,
                                              @RequestBody List<OrderAttributeWrapper> attributes,
                                              @PathVariable("cartId") Long cartId,
                                              @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder) {
        return super.updateOrderAttributes(request, attributes, cartId, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/attributes", method = RequestMethod.DELETE,
        consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public OrderWrapper deleteOrderAttributes(HttpServletRequest request,
                                              @RequestBody List<OrderAttributeWrapper> requestParams,
                                              @PathVariable("cartId") Long cartId,
                                              @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder) {
        return super.deleteOrderAttributes(request, requestParams, cartId, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/item/{itemId}/attributes", method = RequestMethod.PUT,
        consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public OrderWrapper updateProductOptions(HttpServletRequest request,
                                             @RequestBody List<OrderItemAttributeWrapper> requestParams,
                                             @PathVariable("cartId") Long cartId,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder) {
        return super.updateProductOptions(request, requestParams, cartId, itemId, priceOrder);
    }

    @Override
    @RequestMapping(value = "/{cartId}/item/{itemId}/attributes", method = RequestMethod.DELETE,
        consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public OrderWrapper deleteProductOptions(HttpServletRequest request,
                                             @RequestBody(required = false) List<OrderItemAttributeWrapper> requestParams,
                                             @PathVariable("cartId") Long cartId,
                                             @PathVariable("itemId") Long itemId,
                                             @RequestParam(value = "priceOrder", required = false, defaultValue = "true") Boolean priceOrder) {
        return super.deleteProductOptions(request, requestParams, cartId, itemId, priceOrder);
    }
}
