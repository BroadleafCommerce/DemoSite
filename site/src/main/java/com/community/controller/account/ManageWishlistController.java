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
