/*-
 * #%L
 * Community Demo API
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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
package com.community.api.endpoint;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This controller is a part of openapi v3 setup.
 * Its goal to provide custom yaml file with service definition instead of auto-generated.
 * To enable it in default.properties uncomment properties springdoc.api-docs.enabled=false
 * ,springdoc.swagger-ui.url=/api-docs.yaml. Also in uncomment in RestApiMvcConfiguration bean
 * definitions: springDocConfiguration, springDocConfigProperties and comment out definition for customOpenAPI
 */
@Controller
@Hidden
public class GetDocsController {
    @RequestMapping(path="/api-docs.yaml", method = RequestMethod.GET)
    @ResponseBody
    public String getApiDocs() throws IOException {
        File resource = new ClassPathResource("api-docs.yaml").getFile();
        return new String(Files.readAllBytes(resource.toPath()));
    }
}
