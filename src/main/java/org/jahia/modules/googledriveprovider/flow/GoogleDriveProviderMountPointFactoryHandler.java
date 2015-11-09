package org.jahia.modules.googledriveprovider.flow;


import org.jahia.modules.external.admin.mount.AbstractMountPointFactoryHandler;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.utils.i18n.Messages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.webflow.execution.RequestContext;

import javax.jcr.RepositoryException;
import java.io.Serializable;
import java.lang.Override;
import java.util.Locale;


/**
 * Created by david on 03/11/15.
 */
public class GoogleDriveProviderMountPointFactoryHandler extends AbstractMountPointFactoryHandler<GoogleDriveProviderMountPointFactory> {
    private static final Logger logger = LoggerFactory.getLogger(GoogleDriveProviderMountPointFactoryHandler.class);

    private static final long serialVersionUID = 7189210242067838479L;

    private GoogleDriveProviderMountPointFactory googleDriveProviderMountPointFactory;

    public void init(RequestContext requestContext) {
        googleDriveProviderMountPointFactory = new GoogleDriveProviderMountPointFactory();
        try {
            super.init(requestContext, googleDriveProviderMountPointFactory);
        } catch (RepositoryException e) {
            logger.error("Error retrieving mount point", e);
        }
        requestContext.getFlowScope().put("googleDriveProviderFactory", googleDriveProviderMountPointFactory);
    }

    public String getFolderList() {
        JSONObject result = new JSONObject();
        try {
            JSONArray folders = JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<JSONArray>() {
                @Override
                public JSONArray doInJCR(JCRSessionWrapper session) throws RepositoryException {
                    return getSiteFolders(session.getWorkspace());
                }
            });

            result.put("folders", folders);
        } catch (RepositoryException e) {
            logger.error("Error trying to retrieve local folders", e);
        } catch (JSONException e) {
            logger.error("Error trying to construct JSON from local folders", e);
        }

        return result.toString();
    }

    public Boolean save() {
        try {
            return super.save(googleDriveProviderMountPointFactory);
        } catch (RepositoryException e) {
            logger.error("Error saving mount point " + googleDriveProviderMountPointFactory.getName(), e);
        }
        return false;
    }
}
