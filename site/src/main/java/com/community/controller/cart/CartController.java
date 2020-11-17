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

package com.community.controller.cart;


import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.inventory.service.InventoryUnavailableException;
import org.broadleafcommerce.core.order.service.call.AddToCartItem;
import org.broadleafcommerce.core.order.service.call.OrderItemRequestDTO;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.IllegalCartOperationException;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.RequiredAttributeNotProvidedException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.controller.cart.BroadleafCartController;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/cart")
public class CartController extends BroadleafCartController {
    
    @Override
    @RequestMapping("")
    public String cart(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
        String returnPath = super.cart(request, response, model);
        if (isAjaxRequest(request)) {
            returnPath += " :: ajax";
        }
        return returnPath;
    }

    /*
     * Used to return the current representation of the mini-cart
     */
    @RequestMapping("/mini")
    public String miniCart(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
        super.cart(request, response, model);
        return "checkout/partials/miniCart";
    }

    /*
     * Used to return the current representation of cart pricing summary
     */
    @RequestMapping("/summary")
    public String pricingSummary(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "isCheckoutContext", required = false, defaultValue = "false") boolean isCheckoutContext,
            Model model) throws PricingException {
        super.cart(request, response, model);

        model.addAttribute("isCheckoutContext", isCheckoutContext);
        return "cart/partials/cartPricingSummary";
    }

    /*
     * The Heat Clinic does not show the cart when a product is added. Instead, when the product is added via an AJAX
     * POST that requests JSON, we only need to return a few attributes to update the state of the page. The most
     * efficient way to do this is to call the regular add controller method, but instead return a map that contains
     * the necessary attributes. By using the @ResposeBody tag, Spring will automatically use Jackson to convert the
     * returned object into JSON for easy processing via JavaScript.
     */
    @RequestMapping(value = "/add", produces = "application/json")
    public @ResponseBody Map<String, Object> addJson(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("addToCartItem") OrderItemRequestDTO addToCartItem) throws IOException, PricingException, AddToCartException, Exception {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            super.add(request, response, model, addToCartItem);
            responseMap = buildAddResponse(addToCartItem);
            if (isUpdateRequest(request)) {
                responseMap.put("openCart", true);
            }
        } catch (AddToCartException | IllegalArgumentException e) {
            responseMap = buildAddErrorResponse(e);
        }
        return responseMap;
    }

    /*
     * The Heat Clinic does not support adding products with required product options from a category browse page
     * when JavaScript is disabled. When this occurs, we will redirect the user to the full product details page 
     * for the given product so that the required options may be chosen.
     */
    @RequestMapping(value = "/add", produces = { "text/html", "*/*" })
    public String add(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes,
            @ModelAttribute("addToCartItem") AddToCartItem addToCartItem) throws IOException, PricingException, AddToCartException, Exception {
        try {
            return super.add(request, response, model, addToCartItem);
        } catch (AddToCartException e) {
            Product product = catalogService.findProductById(addToCartItem.getProductId());
            return "redirect:" + product.getUrl();
        }
    }
    
    @RequestMapping("/updateQuantity")
    public String updateQuantity(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes,
            @ModelAttribute("addToCartItem") AddToCartItem addToCartItem) throws IOException, PricingException, UpdateCartException, RemoveFromCartException {
        try {
            String returnPath = super.updateQuantity(request, response, model, addToCartItem);
            if (isAjaxRequest(request)) {
                returnPath += " :: ajax";
            }
            return returnPath;
        } catch (UpdateCartException e) {
            if (e.getCause() instanceof InventoryUnavailableException) {
                String errorMessage = "Not enough inventory to fulfill your requested amount of " + addToCartItem.getQuantity();
                return handleCartError(request, model, redirectAttributes, errorMessage);
            } else {
                throw e;
            }
        }
    }

    protected String handleCartError(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes, String errorMessage) {
        // Since there was an exception, the order gets detached from the Hibernate session. This re-attaches it
        CartState.setCart(orderService.findOrderById(CartState.getCart().getId()));
        if (isAjaxRequest(request)) {
            model.addAttribute("errorMessage", errorMessage);
            return getCartView() + " :: ajax";
        } else {
            redirectAttributes.addAttribute("errorMessage", errorMessage);
            return getCartPageRedirect();
        }
    }

    @Override
    @RequestMapping("/remove")
    public String remove(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("addToCartItem") OrderItemRequestDTO addToCartItem) throws IOException, PricingException, RemoveFromCartException {
        String returnPath = super.remove(request, response, model, addToCartItem);
        if (isAjaxRequest(request)) {
            returnPath += " :: ajax";
        }
        return returnPath;
    }
    
    @Override
    @RequestMapping("/empty")
    public String empty(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
        //return super.empty(request, response, model);
        return "ajaxredirect:/";
    }
    
    @Override
    @RequestMapping("/promo")
    public String addPromo(HttpServletRequest request, HttpServletResponse response, Model model,
            @RequestParam("promoCode") String customerOffer) throws IOException, PricingException {
        String returnPath = super.addPromo(request, response, model, customerOffer);

        if (isCheckoutContext(request)) {
            model.addAttribute("includeExtraDataInPartial", true);
        }

        returnPath = getReturnPathForRequest(request, returnPath);
        return returnPath;
    }
    
    @Override
    @RequestMapping("/promo/remove")
    public String removePromo(HttpServletRequest request, HttpServletResponse response, Model model,
            @RequestParam("offerCodeId") Long offerCodeId) throws IOException, PricingException {
        String returnPath = super.removePromo(request, response, model, offerCodeId);

        if (isCheckoutContext(request)) {
            model.addAttribute("includeExtraDataInPartial", true);
        }

        returnPath = getReturnPathForRequest(request, returnPath);
        return returnPath;
    }

    protected String getReturnPathForRequest(HttpServletRequest request, String returnPath) {
        if (isCheckoutContext(request)) {
            returnPath = "checkout/partials/promoCodes";
        } else if (isAjaxRequest(request)) {
            returnPath += " :: ajax";
        }

        return returnPath;
    }

    @Override
    @ExceptionHandler(IllegalCartOperationException.class)
    public @ResponseBody Map<String, String> handleIllegalCartOpException(IllegalCartOperationException ex) {
        return super.handleIllegalCartOpException(ex);
    }

    protected Map<String, Object> buildAddErrorResponse(Exception e) throws Exception {
        Map<String, Object> responseMap = new HashMap<>();

        if (e.getCause() instanceof RequiredAttributeNotProvidedException) {
            RequiredAttributeNotProvidedException exception = (RequiredAttributeNotProvidedException) e.getCause();
            responseMap.put("error", "allOptionsRequired");
            responseMap.put("productId", exception.getProductId());
        } else if (e.getCause() instanceof ProductOptionValidationException) {
            ProductOptionValidationException exception = (ProductOptionValidationException) e.getCause();
            responseMap.put("error", "productOptionValidationError");
            responseMap.put("errorCode", exception.getErrorCode());
            responseMap.put("errorMessage", exception.getMessage());
            //blMessages.getMessage(exception.get, lfocale))
        } else if (e.getCause() instanceof InventoryUnavailableException) {
            responseMap.put("error", "inventoryUnavailable");
        } else if (e instanceof IllegalArgumentException) {
            IllegalArgumentException exception = (IllegalArgumentException) e;
            responseMap.put("error", "addOnValidationError");
            responseMap.put("errorMessage", exception.getMessage());
        } else {
            throw e;
        }
        return responseMap;
    }

    protected Map<String, Object> buildAddResponse(OrderItemRequestDTO addToCartItem) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("productName", catalogService.findProductById(addToCartItem.getProductId()).getName());
        responseMap.put("quantityAdded", addToCartItem.getQuantity());
        responseMap.put("cartItemCount", String.valueOf(CartState.getCart().getItemCount()));
        if (addToCartItem.getItemAttributes() == null || addToCartItem.getItemAttributes().size() == 0) {
            // We don't want to return a productId to hide actions for when it is a product that has multiple
            // product options. The user may want the product in another version of the options as well.
            responseMap.put("productId", addToCartItem.getProductId());
        }
        return responseMap;
    }
}
