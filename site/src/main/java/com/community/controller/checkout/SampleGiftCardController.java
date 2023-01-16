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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.model.GiftCardInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.core.web.controller.checkout.AbstractCheckoutController;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a stub implementation to handle gift card processing.
 * Please contact us to learn more about our AccountCredit module that handles both Gift Cards and Customer Credit.
 * https://www.broadleafcommerce.com/contact
 *
 * This should NOT be used in production, and is meant solely for demonstration
 * purposes only.
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Controller("blSamplePaymentGatewayGiftCardController")
@RequestMapping("/" + SampleGiftCardController.GATEWAY_CONTEXT_KEY)
public class SampleGiftCardController extends AbstractCheckoutController {

    protected static final Log LOG = LogFactory.getLog(SampleGiftCardController.class);
    protected static final String GATEWAY_CONTEXT_KEY = "sample-giftcard";

    @RequestMapping(value="/apply", method = RequestMethod.POST)
    public String applyGiftCard(HttpServletRequest request, HttpServletResponse response, Model model,
                                @ModelAttribute("orderInfoForm") OrderInfoForm orderInfoForm,
                                @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingForm,
                                @ModelAttribute("billingInfoForm") BillingInfoForm billingForm,
                                @ModelAttribute("giftCardInfoForm") GiftCardInfoForm giftCardInfoForm,
                                BindingResult result){
        Order cart = CartState.getCart();

        giftCardInfoFormValidator.validate(giftCardInfoForm, result);
        if (!result.hasErrors()) {
            result.reject("giftCardNumber", "The Gift Card module is not enabled. Please contact us for more information about our AccountCredit Module (https://www.broadleafcommerce.com/contact)");
        }

        if (!(cart instanceof NullOrderImpl)) {
            model.addAttribute("orderMultishipOptions",
                    orderMultishipOptionService.getOrGenerateOrderMultishipOptions(cart));
            model.addAttribute("paymentRequestDTO",
                    dtoTranslationService.translateOrder(cart));
        }

        populateModelWithReferenceData(request, model);

        return getCheckoutView();
    }

    @Override
    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
    }

}
