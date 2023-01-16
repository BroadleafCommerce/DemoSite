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
package com.community.controller.contactus;

import org.broadleafcommerce.common.email.domain.EmailTargetImpl;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

import javax.annotation.Resource;

@Controller("blContactUsController")
public class ContactUsController {

	@Value("${site.emailAddress}")
	protected String targetEmailAddress;

	@Resource(name = "blEmailService")
	protected EmailService emailService;

	@RequestMapping(value = "/contactus/success", method = RequestMethod.POST)
	public String sendConfirmationEmail(@RequestParam("name") String name,
			@RequestParam("emailAddress") String emailAddress,
			@RequestParam("comments") String comments) {
		HashMap<String, Object> vars = new HashMap<String, Object>();
		vars.put("name", name);
		vars.put("comments", comments);
		vars.put("emailAddress", emailAddress);
		
		EmailInfo emailInfo = new EmailInfo();
		
		emailInfo.setFromAddress(emailAddress);
		emailInfo.setSubject("Message from " + name);
		emailInfo.setMessageBody("Name: " + name + "<br />Email: " + emailAddress + "<br />Comments: " + comments);
		EmailTargetImpl emailTarget = new EmailTargetImpl();
		
		emailTarget.setEmailAddress(targetEmailAddress);
		try {
			emailService.sendBasicEmail(emailInfo, emailTarget, vars);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "redirect:/contactus";

		}
		
		return "contactus/success";

	}

	@RequestMapping(value="/contactus")
	public String index() {
		return "contactus/contactus";
	}
}
