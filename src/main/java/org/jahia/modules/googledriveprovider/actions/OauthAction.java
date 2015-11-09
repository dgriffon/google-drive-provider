package org.jahia.modules.googledriveprovider.actions;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by david on 06/11/15.
 * This will be done by the handler
 */
public class OauthAction extends Action {
    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> map, URLResolver urlResolver) throws Exception {
        // Register the user
        if (httpServletRequest.getQueryString().contains("getAuthorizeToken")) {
            StringBuilder url = new StringBuilder("https://accounts.google.com/o/oauth2/auth?");
            url.append("client_id=262794542454-79dh7g7v0aqtideg2t3fhkuk1f35d2jn.apps.googleusercontent.com");
            url.append("&redirect_uri=)").append(URLEncoder.encode("http://localhost:8080/jahia/cms/admin/default/en/settings.oauth.do", "UTF-8"));
            url.append("&state=ok");
            renderContext.getResponse().sendRedirect(url.toString());
            return null;
        }
        // Do somehting for debugging
        String s = "toto";
        return null;

    }
}
