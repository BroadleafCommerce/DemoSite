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
        File resource = new ClassPathResource(
                "api-docs.yaml").getFile();
        String text = new String(
                Files.readAllBytes(resource.toPath()));
        return text;
    }
}
