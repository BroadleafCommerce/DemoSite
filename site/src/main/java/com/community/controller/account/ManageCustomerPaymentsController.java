package com.community.controller.account;

import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.broadleafcommerce.core.web.controller.account.BroadleafManageCustomerPaymentsController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chris Kittrell (ckittrell)
 * @author Jacob Mitash
 */
@ConditionalOnProperty(name = "saved.customer.payments.enabled", matchIfMissing = true)
@Controller
@RequestMapping("/account/payments")
public class ManageCustomerPaymentsController extends BroadleafManageCustomerPaymentsController {

    @Override
    @RequestMapping(method = RequestMethod.GET)
    public String viewCustomerPayments(HttpServletRequest request, Model model,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentInfoForm) {
        return super.viewCustomerPayments(request, model, paymentInfoForm);
    }

    @Override
    @RequestMapping(method = RequestMethod.POST)
    public String addCustomerPayment(HttpServletRequest request, Model model,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentInfoForm, BindingResult bindingResult) {
        return super.addCustomerPayment(request, model, paymentInfoForm, bindingResult);
    }

    @RequestMapping(value = "/remove/{customerPaymentId}", method = RequestMethod.POST)
    public String removeCustomerPayment(HttpServletRequest request, Model model,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentInfoForm,
            @PathVariable("customerPaymentId") Long customerPaymentId) {
        return super.removeCustomerPayment(request, model, customerPaymentId);
    }

    @RequestMapping(value = "/default/{customerPaymentId}", method = RequestMethod.POST)
    public String makeDefaultCustomerPayment(HttpServletRequest request, Model model,
            @ModelAttribute("paymentInfoForm") PaymentInfoForm paymentInfoForm,
            @PathVariable("customerPaymentId") Long customerPaymentId) {
        return super.makeDefaultCustomerPayment(request, model, customerPaymentId);
    }
}

