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
 * http://www.broadleafcommerce.com/contact
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
            result.reject("giftCardNumber", "The Gift Card module is not enabled. Please contact us for more information about our AccountCredit Module (http://www.broadleafcommerce.com/contact)");
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
