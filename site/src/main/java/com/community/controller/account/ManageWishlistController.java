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

package com.community.controller.account;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.controller.account.BroadleafManageWishlistController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/account/wishlist")
public class ManageWishlistController extends BroadleafManageWishlistController {

    public static final String WISHLIST_ORDER_NAME = "wishlist";

    @RequestMapping(method = RequestMethod.GET)
    public String viewAccountWishlist(HttpServletRequest request, HttpServletResponse response, Model model) {
        return super.viewWishlist(request, response, model, WISHLIST_ORDER_NAME);
    }
    
    @RequestMapping(value = "/add", produces = "application/json")
    public @ResponseBody Map<String, Object> addJson(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("addToCartItem") OrderItemRequestDTO addToCartItem) throws IOException, PricingException, AddToCartException {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        try {
            super.add(request, response, model, addToCartItem, WISHLIST_ORDER_NAME);
            
            responseMap.put("productName", catalogService.findProductById(addToCartItem.getProductId()).getName());
            responseMap.put("quantityAdded", addToCartItem.getQuantity());
            if (addToCartItem.getItemAttributes() == null || addToCartItem.getItemAttributes().size() == 0) {
                // We don't want to return a productId to hide actions for when it is a product that has multiple
                // product options. The user may want the product in another version of the options as well.
                responseMap.put("productId", addToCartItem.getProductId());
            }
        } catch (AddToCartException e) {
            if (e.getCause() instanceof RequiredAttributeNotProvidedException) {
                responseMap.put("error", "allOptionsRequired");
            } else {
                throw e;
            }
        }
        
        return responseMap;
    }
    
    /*
     * The Heat Clinic does not support adding products with required product options from a category browse page
     * when JavaScript is disabled. When this occurs, we will redirect the user to the full product details page 
     * for the given product so that the required options may be chosen.
     */
    @RequestMapping(value = "/add", produces = "text/html")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("addToCartItem") OrderItemRequestDTO addToCartItem) throws IOException, PricingException, AddToCartException {
        try {
            return super.add(request, response, model, addToCartItem, WISHLIST_ORDER_NAME);
        } catch (AddToCartException e) {
            if (e.getCause() instanceof RequiredAttributeNotProvidedException) {
                Product product = catalogService.findProductById(addToCartItem.getProductId());
                return "redirect:" + product.getUrl();
            } else {
                throw e;
            }
        }
    }

    @RequestMapping("/updateQuantity")
    public String updateQuantityFromWishlist(HttpServletRequest request, HttpServletResponse response, Model model,
                                 OrderItemRequestDTO itemRequest) throws IOException, UpdateCartException, PricingException, RemoveFromCartException {
        String result = super.updateQuantityInWishlist(request, response, model, WISHLIST_ORDER_NAME, itemRequest);
        return result  + " :: ajax";
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public String removeItemFromWishlist(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("orderItemId") Long itemId) throws RemoveFromCartException {
        return super.removeItemFromWishlist(request, response, model, WISHLIST_ORDER_NAME, itemId);
    }

    @RequestMapping(value = "/moveItemToCart", method = RequestMethod.POST)
    public String moveItemToCart(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("itemId") Long itemId) throws IOException, PricingException, AddToCartException, RemoveFromCartException {
        return super.moveItemToCart(request, response, model, WISHLIST_ORDER_NAME, itemId);   
    }

    @RequestMapping(value = "/moveListToCart", method = RequestMethod.POST)
    public String moveListToCart(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException, PricingException, AddToCartException, RemoveFromCartException {
        return super.moveListToCart(request, response, model, WISHLIST_ORDER_NAME);  
    }

}
