/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.community.controller.account;

import org.broadleafcommerce.common.web.controller.BroadleafRedirectController;
import org.broadleafcommerce.common.web.security.BroadleafAuthenticationSuccessRedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The controller expects a session attribute to be set called BLC_REDIRECT_URL and 
 * that it is being called from an Ajax redirect process.
 * 
 * It would be unexpected for an implementor to modify this class or the corresponding view
 * blcRedirect.html.  
 * 
 * The purpose of this class is to support ajax redirects after a successful login. 
 * 
 * @see BroadleafAuthenticationSuccessRedirectStrategy
 * 
 * @author bpolster
 */
@Controller
public class RedirectController extends BroadleafRedirectController {
    
    @RequestMapping("/redirect")
    public String redirect(HttpServletRequest request, HttpServletResponse response, Model model) {
        return super.redirect(request, response, model);
    }
}
