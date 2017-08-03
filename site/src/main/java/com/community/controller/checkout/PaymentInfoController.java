/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
