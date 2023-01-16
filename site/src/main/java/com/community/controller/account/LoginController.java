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

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.web.controller.account.BroadleafLoginController;
import org.broadleafcommerce.core.web.controller.account.ResetPasswordForm;
import org.broadleafcommerce.profile.web.core.form.RegisterCustomerForm;
import org.broadleafcommerce.profile.web.core.service.register.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The controller responsible for all actions involving logging a customer in
 */
@Controller
public class LoginController extends BroadleafLoginController {

    @Resource(name = "blRegistrationService")
    RegistrationService registrationService;

    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        String loginView = super.login(request, response, model);

        RegisterCustomerForm registrationForm = buildRegistrationForm();
        model.addAttribute("registrationForm", registrationForm);

        return loginView;
    }

    protected RegisterCustomerForm buildRegistrationForm() {
        RegisterCustomerForm registrationForm = registrationService.initCustomerRegistrationForm();
        registrationService.addRedirectUrlToForm(registrationForm);

        return registrationForm;
    }

    @RequestMapping(value="/login/forgotPassword", method=RequestMethod.GET)
    public String forgotPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
        return super.forgotPassword(request, response, model);
    }
    
    @RequestMapping(value="/login/forgotPassword", method=RequestMethod.POST)
    public String processForgotPassword(@RequestParam("emailAddress") String emailAddress, HttpServletRequest request, Model model) {
        return super.processForgotPassword(emailAddress, request, model);
    }   

    @RequestMapping(value="/login/resetPassword", method=RequestMethod.GET)
    public String resetPassword(HttpServletRequest request, HttpServletResponse response, Model model) {
        return super.resetPassword(request, response, model);
    }   
    
    @RequestMapping(value="/login/resetPassword", method=RequestMethod.POST)
    public String processResetPassword(@ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm, HttpServletRequest request, HttpServletResponse response, Model model, BindingResult errors) throws ServiceException {
        return super.processResetPassword(resetPasswordForm, request, response, model, errors);
    }   
    
    @Override
    public String getResetPasswordUrl(HttpServletRequest request) {     
        String url = request.getScheme() + "://" + request.getServerName() + getResetPasswordPort(request, request.getScheme());
        
        if (request.getContextPath() != null && ! "".equals(request.getContextPath())) {
            url = url + request.getContextPath() + "/login/resetPassword";
        } else {
            url = url + "/login/resetPassword";
        }
        return url;
    }
}
