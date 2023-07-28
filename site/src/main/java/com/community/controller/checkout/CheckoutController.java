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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.common.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.pricing.service.workflow.OfferActivity;
import org.broadleafcommerce.core.web.checkout.model.CustomerCreditInfoForm;
import org.broadleafcommerce.core.web.checkout.model.GiftCardInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.core.web.checkout.service.CheckoutFormService;
import org.broadleafcommerce.core.web.checkout.stage.CheckoutStageType;
import org.broadleafcommerce.core.web.controller.checkout.BroadleafCheckoutController;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.broadleafcommerce.vendor.sample.service.payment.SamplePaymentGatewayConstants;
import org.broadleafcommerce.vendor.sample.service.payment.SamplePaymentGatewayType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CheckoutController extends BroadleafCheckoutController {

    public static final String REDIRECT_CHECKOUT_LOGIN = "redirect:/checkout/login";
    public static final String GUEST_CHECKOUT = "guest-checkout";

    @Resource(name = "blCheckoutFormService")
    protected CheckoutFormService checkoutFormService;

    @RequestMapping(value = "/checkout", method = RequestMethod.GET)
    public String checkout(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("orderInfoForm") OrderInfoForm orderInfoForm,
            @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingInfoForm,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentInfoForm,
            @ModelAttribute("giftCardInfoForm") GiftCardInfoForm giftCardInfoForm,
            @ModelAttribute("customerCreditInfoForm") CustomerCreditInfoForm customerCreditInfoForm,
            RedirectAttributes redirectAttributes) {
        if (shouldRedirectToCheckoutLogin(request)) {
            return REDIRECT_CHECKOUT_LOGIN;
        }

        String checkoutView = super.checkout(request, response, model, redirectAttributes);
        model.addAttribute(ACTIVE_STAGE, getActiveCheckoutStage(request));

        checkoutFormService.prePopulateInfoForms(shippingInfoForm, paymentInfoForm);
        checkoutFormService.determineIfSavedAddressIsSelected(model, shippingInfoForm, paymentInfoForm);

        return checkoutView;
    }

    protected boolean shouldRedirectToCheckoutLogin(HttpServletRequest request) {
        Order cart = CartState.getCart();

        boolean customerHasEmptyCart = CollectionUtils.isEmpty(cart.getOrderItems());
        boolean isCustomerAnonymous = CustomerState.getCustomer().isAnonymous();
        boolean hasGuestCheckoutParam = request.getParameter(GUEST_CHECKOUT) != null;
        boolean cartHasThirdPartyPayment = cartStateService.cartHasThirdPartyPayment();

        return !customerHasEmptyCart && isCustomerAnonymous && !hasGuestCheckoutParam && !cartHasThirdPartyPayment;
    }

    protected String getActiveCheckoutStage(HttpServletRequest request) {
        String activeStage = request.getParameter(ACTIVE_STAGE);

        if (activeStage == null) {
            if (!cartStateService.cartHasPopulatedShippingAddress()) {
                activeStage = CheckoutStageType.SHIPPING_INFO.getType();
            } else if ((!cartStateService.cartHasPopulatedBillingAddress() || cartStateService.cartHasTemporaryCreditCard())
                    && !cartStateService.cartHasThirdPartyPayment()) {
                activeStage = CheckoutStageType.PAYMENT_INFO.getType();
            } else {
                activeStage = CheckoutStageType.REVIEW.getType();
            }
        }

        return activeStage;
    }

    @RequestMapping(value = "/checkout/login", method = RequestMethod.GET)
    public String checkoutLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("successUrl", "/checkout");
        return "checkout/checkoutLogin";
    }

    @RequestMapping(value = "/checkout/{stage}", method = RequestMethod.GET)
    public String getCheckoutStagePartial(HttpServletRequest request, HttpServletResponse response, Model model,
            @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingInfoForm,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentInfoForm,
            @ModelAttribute("giftCardInfoForm") GiftCardInfoForm giftCardInfoForm,
            @ModelAttribute("customerCreditInfoForm") CustomerCreditInfoForm customerCreditInfoForm,
            @PathVariable("stage") String activeStage,
            RedirectAttributes redirectAttributes) {

        checkoutFormService.prePopulateInfoForms(shippingInfoForm, paymentInfoForm);
        checkoutFormService.determineIfSavedAddressIsSelected(model, shippingInfoForm, paymentInfoForm);

        return super.getCheckoutStagePartial(request, response, model, activeStage, redirectAttributes);
    }

    @RequestMapping(value = "/checkout/savedetails", method = RequestMethod.POST)
    public String saveGlobalOrderDetails(HttpServletRequest request, Model model,
            @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingInfoForm,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentInfoForm,
            @ModelAttribute("giftCardInfoForm") GiftCardInfoForm giftCardInfoForm,
            @ModelAttribute("orderInfoForm") OrderInfoForm orderInfoForm, BindingResult result) throws ServiceException {
        return super.saveGlobalOrderDetails(request, model, orderInfoForm, result);
    }

    @RequestMapping(value = "/checkout/complete", method = RequestMethod.POST)
    public String processCompleteCheckoutOrderFinalized(RedirectAttributes redirectAttributes,
                                                        @RequestParam(value = "payment_method_nonce", required = false) String nonce) throws PaymentException, PricingException {
        Order cart = CartState.getCart();
        if (!StringUtils.isEmpty(nonce)) {
            OrderPayment paymentNonce = createNonceOrderPayment(cart);

            //Populate Billing Address per UI requirements
            //For this example, we'll copy the address from the temporary Credit Card's Billing address and archive the payment,
            // (since Heat Clinic's checkout template saves and validates the address in a previous section).
            OrderPayment tempPayment = getTempCreditCardPayment(cart);
            if (tempPayment != null) {
                paymentNonce.setBillingAddress(addressService.copyAddress(tempPayment.getBillingAddress()));
                orderService.removePaymentFromOrder(cart, tempPayment);
            }

            PaymentTransaction transaction = createUnconfirmedTransaction(cart, nonce);
            transaction.setOrderPayment(paymentNonce);
            paymentNonce.addTransaction(transaction);
            orderService.addPaymentToOrder(cart, paymentNonce, null);
            //set variable so if some offer code is removed we can notify buyer about it.
            BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().put(OfferActivity.FINALIZE_CHECKOUT, Boolean.TRUE);
            orderService.save(cart, true);
        }else if(CollectionUtils.isNotEmpty(cart.getPayments())){
            //set variable so if some offer code is removed we can notify buyer about it.
            BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().put(OfferActivity.FINALIZE_CHECKOUT, Boolean.TRUE);
            //price order in case some offer is expired
            orderService.save(cart, true);
        }

        return super.processCompleteCheckoutOrderFinalized(redirectAttributes);
    }


    protected OrderPayment getTempCreditCardPayment(Order cart) {
        OrderPayment tempPayment = null;
        for (OrderPayment payment : cart.getPayments()) {
            if (PaymentGatewayType.TEMPORARY.equals(payment.getGatewayType()) &&
                    PaymentType.CREDIT_CARD.equals(payment.getType())) {
                tempPayment = payment;
                break;
            }
        }
        return tempPayment;
    }

    protected OrderPayment createNonceOrderPayment(Order cart) {
        OrderPayment paymentNonce = orderPaymentService.create();
        paymentNonce.setType(PaymentType.CREDIT_CARD);
        paymentNonce.setPaymentGatewayType(SamplePaymentGatewayType.NULL_GATEWAY);
        paymentNonce.setAmount(cart.getTotalAfterAppliedPayments());
        paymentNonce.setOrder(cart);
        return paymentNonce;
    }

    protected PaymentTransaction createUnconfirmedTransaction(Order cart, String nonce) {
        PaymentTransaction transaction = orderPaymentService.createTransaction();
        transaction.setAmount(cart.getTotalAfterAppliedPayments());
        transaction.setRawResponse("Sample Payment Nonce");
        transaction.setSuccess(true);
        transaction.setType(PaymentTransactionType.UNCONFIRMED);
        transaction.getAdditionalFields().put(SamplePaymentGatewayConstants.PAYMENT_METHOD_NONCE, nonce);
        return transaction;
    }

    @RequestMapping(value = "/checkout/cod/complete", method = RequestMethod.POST)
    public String processPassthroughCheckout(RedirectAttributes redirectAttributes)
            throws PaymentException, PricingException {
        return super.processPassthroughCheckout(redirectAttributes, PaymentType.COD);
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
    }
    
}
