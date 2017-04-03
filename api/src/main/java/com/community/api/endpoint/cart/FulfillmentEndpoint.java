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

import com.broadleafcommerce.rest.api.wrapper.AddressWrapper;
import com.broadleafcommerce.rest.api.wrapper.FulfillmentGroupItemWrapper;
import com.broadleafcommerce.rest.api.wrapper.FulfillmentGroupWrapper;
import com.broadleafcommerce.rest.api.wrapper.FulfillmentOptionWrapper;
import com.broadleafcommerce.rest.api.wrapper.OrderWrapper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * This is a reference REST API endpoint for fulfillment groups. This can be modified, used as is, or removed. 
 * The purpose is to provide an out of the box RESTful fulfillment group service implementation, but also 
 * to allow the implementor to have fine control over the actual API, URIs, and general JAX-RS annotations.
 * 
 * @author Kelly Tisdell
 *
 */
@RestController
@RequestMapping(value = "/shipping/",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class FulfillmentEndpoint extends com.broadleafcommerce.rest.api.endpoint.order.FulfillmentEndpoint {

    @Override
    @RequestMapping(value = "{cartId}/groups", method = RequestMethod.GET)
    public List<FulfillmentGroupWrapper> findFulfillmentGroupsForOrder(HttpServletRequest request, @PathVariable("cartId") Long cartId) {
        return super.findFulfillmentGroupsForOrder(request, cartId);
    }
    
    @Override
    @RequestMapping(value = "options", method = RequestMethod.GET)
    public List<FulfillmentOptionWrapper> findFulfillmentOptions(
            HttpServletRequest request, 
            @RequestParam("fulfillmentType") String fulfillmentType) {
        return super.findFulfillmentOptions(request, fulfillmentType);
    }
    
    @Override
    @RequestMapping(value = "{cartId}/group/{fulfillmentGroupId}/item", method = RequestMethod.POST)
    public FulfillmentGroupWrapper addItemToFulfillmentGroup(HttpServletRequest request,
            @PathVariable("fulfillmentGroupId") Long fulfillmentGroupId,
            FulfillmentGroupItemWrapper wrapper,
            @RequestParam(value = "priceOrder", defaultValue = "true") boolean priceOrder,
            @PathVariable(value = "cartId") Long cartId) {
        return super.addItemToFulfillmentGroup(request, fulfillmentGroupId, wrapper, priceOrder, cartId);
    }

    @Override
    @RequestMapping(value = "{cartId}/group/{fulfillmentGroupId}/option/{fulfillmentOptionId}", method = RequestMethod.PUT)
    public FulfillmentGroupWrapper addFulfillmentOptionToFulfillmentGroup(
            HttpServletRequest request, 
            @PathVariable("fulfillmentGroupId") Long fulfillmentGroupId,
            @PathVariable("fulfillmentOptionId") Long fulfillmentOptionId, 
            @RequestParam(value = "priceOrder", defaultValue = "true") boolean priceOrder,
            @PathVariable(value = "cartId") Long cartId) {
        return super.addFulfillmentOptionToFulfillmentGroup(request,
                fulfillmentGroupId, fulfillmentOptionId, priceOrder, cartId);
    }
    
    @Override
    @RequestMapping(value = "{cartId}/{fulfillmentGroupId}/address", method = RequestMethod.PUT)
    public FulfillmentGroupWrapper updateFulfillmentGroupAddress(HttpServletRequest request,
                                                                 @PathVariable("fulfillmentGroupId") Long fulfillmentGroupId,
                                                                 @PathVariable("cartId") Long cartId,
                                                                 @RequestBody AddressWrapper address) {
        return super.updateFulfillmentGroupAddress(request, fulfillmentGroupId, cartId, address);
    }
    @Override
    @RequestMapping(value = "{cartId}/group", method = RequestMethod.POST)
    public FulfillmentGroupWrapper addFulfillmentGroupToOrder(HttpServletRequest request,
            @RequestParam FulfillmentGroupWrapper wrapper,
            @RequestParam(value = "priceOrder", defaultValue = "true") boolean priceOrder,
            @PathVariable("cartId") Long cartId) {
        return super.addFulfillmentGroupToOrder(request, wrapper, priceOrder, cartId);
    }
    
    @Override
    @RequestMapping(value = "{cartId}/groups", method = RequestMethod.DELETE)
    public OrderWrapper removeAllFulfillmentGroupsFromOrder(HttpServletRequest request,
            @RequestParam(value = "priceOrder", defaultValue = "true") boolean priceOrder,
            @PathVariable("cartId") Long cartId) {
        return super.removeAllFulfillmentGroupsFromOrder(request, priceOrder, cartId);
    }

    @Override
    @RequestMapping(value = "{cartId}/group/{fulfillmentGroupId}/item/{itemId}", method = RequestMethod.DELETE)
    public FulfillmentGroupWrapper removeOrderItemFromFulfillmentGroup(HttpServletRequest request,
            @PathVariable("fulfillmentGroupId") Long fulfillmentGroupId,
            @PathVariable("itemId") Long itemId,
            @PathVariable("cartId") Long cartId) {
        return super.removeOrderItemFromFulfillmentGroup(request, fulfillmentGroupId, itemId, cartId);
    }
    
    @Override
    @RequestMapping(value = "{cartId}/group/{fulfillmentGroupId}", method = RequestMethod.DELETE)
    public List<FulfillmentGroupWrapper> removeFulfillmentGroupFromOrder(HttpServletRequest request,
            @PathVariable("fulfillmentGroupId") Long fulfillmentGroupId,
            @PathVariable("cartId") Long cartId) {
        return super.removeFulfillmentGroupFromOrder(request, fulfillmentGroupId, cartId);
    }
    
}
