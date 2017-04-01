package com.community.controller.seo;

import org.broadleafcommerce.common.sitemap.controller.BroadleafSiteMapController;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Controller to generate and retrieve site map files.
 * 
 * @author Joshua Skorton (jskorton)
 */
@Controller
public class SiteMapController extends BroadleafSiteMapController {
    
    /**
     * Retrieves a site map file
     * 
     * @param request
     * @param response
     * @param model
     * @param fileName
     * @return
     */
    @RequestMapping(value = { "/sitemap*.xml", "sitemap*.gz" })
    @ResponseBody
    public FileSystemResource retrieveSiteMapIndex(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        return super.retrieveSiteMapFile(request, response);
    }
}
