/*
 * Copyright 2008-2013 the original author or authors.
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

package com.community.workflow.checkout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.HashMap;

import javax.annotation.Resource;


/**
 * Send order confirmation email
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @author Joshua Skorton (jskorton)
 */
public class SendOrderConfirmationEmailActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    protected static final Log LOG = LogFactory.getLog(SendOrderConfirmationEmailActivity.class);

    @Resource(name = "blEmailService")
    protected EmailService emailService;
    
    @Resource(name = "blOrderConfirmationEmailInfo")
    protected EmailInfo orderConfirmationEmailInfo;
    
    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Order order = context.getSeedData().getOrder();
        HashMap<String, Object> vars = new HashMap<String, Object>();
        vars.put("customer", order.getCustomer());
        vars.put("orderNumber", order.getOrderNumber());
        vars.put("order", order);

        //Email service failing should not trigger rollback
        try {
            emailService.sendTemplateEmail(order.getEmailAddress(), getOrderConfirmationEmailInfo(), vars);
        } catch (Exception e) {
            LOG.error(e);
        }
        return context;
    }
    
    public EmailInfo getOrderConfirmationEmailInfo() {
        return orderConfirmationEmailInfo;
    }

    public void setOrderConfirmationEmailInfo(EmailInfo orderConfirmationEmailInfo) {
        this.orderConfirmationEmailInfo = orderConfirmationEmailInfo;
    }

}

