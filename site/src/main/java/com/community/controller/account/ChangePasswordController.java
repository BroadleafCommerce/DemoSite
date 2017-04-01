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

package com.community.controller.account;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.web.controller.account.BroadleafChangePasswordController;
import org.broadleafcommerce.core.web.controller.account.ChangePasswordForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/account/password")
public class ChangePasswordController extends BroadleafChangePasswordController {

    @RequestMapping(method = RequestMethod.GET)
    public String viewChangePassword(HttpServletRequest request, Model model, @ModelAttribute("changePasswordForm") ChangePasswordForm form) {
        return super.viewChangePassword(request, model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processChangePassword(HttpServletRequest request, Model model, @ModelAttribute("changePasswordForm") ChangePasswordForm form, BindingResult result, RedirectAttributes redirectAttributes) throws ServiceException {
        return super.processChangePassword(request, model, form, result, redirectAttributes);
    }
    
}
