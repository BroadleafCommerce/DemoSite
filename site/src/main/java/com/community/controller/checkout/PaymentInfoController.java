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
import org.broadleafcommerce.core.web.checkout.model.GiftCardInfoForm;
import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.core.web.checkout.service.CheckoutFormService;
import org.broadleafcommerce.core.web.checkout.stage.CheckoutStageType;
import org.broadleafcommerce.core.web.controller.checkout.BroadleafPaymentInfoController;
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
public class PaymentInfoController extends BroadleafPaymentInfoController {

    @Resource(name = "blCheckoutFormService")
    protected CheckoutFormService checkoutFormService;

    @RequestMapping(value="/checkout/payment", method = RequestMethod.POST)
    public String savePaymentInfo(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingForm,
            @ModelAttribute("giftCardInfoForm") GiftCardInfoForm giftCardInfoForm,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentForm,
            BindingResult result) throws PricingException, ServiceException {
        super.savePaymentInfo(request, response, model, paymentForm, result);

        prePopulateForms(shippingForm, paymentForm, result);

        String nextActiveStage = result.hasErrors() ?
                CheckoutStageType.PAYMENT_INFO.getType() : CheckoutStageType.REVIEW.getType();

        model.addAttribute(ACTIVE_STAGE, nextActiveStage);
        return getCheckoutStagesPartial();
    }

    @RequestMapping(value="/checkout/payment/billing", method = RequestMethod.POST)
    public String saveBillingAddress(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingForm,
            @ModelAttribute("giftCardInfoForm") GiftCardInfoForm giftCardInfoForm,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentForm,
            BindingResult result) throws PricingException, ServiceException {
        super.saveBillingAddress(request, response, model, paymentForm, result);

        prePopulateForms(shippingForm, paymentForm, result);

        String nextActiveStage = result.hasErrors() ?
                CheckoutStageType.PAYMENT_INFO.getType() : CheckoutStageType.REVIEW.getType();

        model.addAttribute(ACTIVE_STAGE, nextActiveStage);
        return getCheckoutStagesPartial();
    }

    protected void prePopulateForms(ShippingInfoForm shippingForm, PaymentInfoForm paymentForm, BindingResult result) {
        Order cart = CartState.getCart();
        checkoutFormService.prePopulateShippingInfoForm(shippingForm, cart);

        if (!result.hasErrors()) {
            checkoutFormService.prePopulatePaymentInfoForm(paymentForm, shippingForm, cart);
        }
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
    }

}
