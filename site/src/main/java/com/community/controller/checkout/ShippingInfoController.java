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

package com.community.controller.checkout;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.checkout.model.OrderMultishipOptionForm;
import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.core.web.checkout.service.CheckoutFormService;
import org.broadleafcommerce.core.web.checkout.stage.CheckoutStageType;
import org.broadleafcommerce.core.web.controller.checkout.BroadleafShippingInfoController;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ShippingInfoController extends BroadleafShippingInfoController {

    @Resource(name = "blCheckoutFormService")
    protected CheckoutFormService checkoutFormService;

    @RequestMapping(value="/checkout/singleship", method = RequestMethod.GET)
    public String convertToSingleship(HttpServletRequest request, HttpServletResponse response, Model model) throws PricingException {
        return super.convertToSingleship(request, response, model);
    }

    @RequestMapping(value="/checkout/singleship", method = RequestMethod.POST)
    public String saveSingleShip(HttpServletRequest request, HttpServletResponse response, Model model,
                                 @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingInfoForm,
                                 BindingResult result)
            throws PricingException, ServiceException {
        super.saveSingleShip(request, response, model, shippingInfoForm, result);

        Order cart = CartState.getCart();
        PaymentInfoForm paymentInfoForm = new PaymentInfoForm();
        model.addAttribute("paymentInfoForm", paymentInfoForm);

        checkoutFormService.prePopulatePaymentInfoForm(paymentInfoForm, shippingInfoForm, cart);

        if (!result.hasErrors()) {
            checkoutFormService.prePopulateShippingInfoForm(shippingInfoForm, cart);
            checkoutFormService.determineIfSavedAddressIsSelected(model, shippingInfoForm, paymentInfoForm);
        }

        String nextActiveStage = CheckoutStageType.PAYMENT_INFO.getType();
        if (result.hasErrors()) {
            nextActiveStage = CheckoutStageType.SHIPPING_INFO.getType();
        } else if (cartStateService.cartHasThirdPartyPayment()) {
            nextActiveStage = CheckoutStageType.REVIEW.getType();
        }

        model.addAttribute(ACTIVE_STAGE, nextActiveStage);
        return getCheckoutStagesPartial();
    }

    @RequestMapping(value = "/checkout/multiship", method = RequestMethod.GET)
    public String showMultiship(HttpServletRequest request, HttpServletResponse response, Model model,
                                @ModelAttribute("orderMultishipOptionForm") OrderMultishipOptionForm orderMultishipOptionForm,
                                BindingResult result) throws PricingException {
        return super.showMultiship(request, response, model);
    }

    @RequestMapping(value = "/checkout/multiship", method = RequestMethod.POST)
    public String saveMultiship(HttpServletRequest request, HttpServletResponse response, Model model,
                                @ModelAttribute("orderMultishipOptionForm") OrderMultishipOptionForm orderMultishipOptionForm,
                                BindingResult result) throws PricingException, ServiceException {
        return super.saveMultiship(request, response, model, orderMultishipOptionForm, result);
    }

    @RequestMapping(value = "/checkout/add-address", method = RequestMethod.GET)
    public String showMultishipAddAddress(HttpServletRequest request, HttpServletResponse response, Model model,
                                          @ModelAttribute("addressForm") ShippingInfoForm addressForm, BindingResult result) {
        return super.showMultishipAddAddress(request, response, model);
    }

    @RequestMapping(value = "/checkout/add-address", method = RequestMethod.POST)
    public String saveMultishipAddAddress(HttpServletRequest request, HttpServletResponse response, Model model,
                                          @ModelAttribute("addressForm") ShippingInfoForm addressForm, BindingResult result) throws ServiceException {
        return super.saveMultishipAddAddress(request, response, model, addressForm, result);
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
    }

}
