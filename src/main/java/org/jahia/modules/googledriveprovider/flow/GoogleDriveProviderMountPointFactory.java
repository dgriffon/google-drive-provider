package org.jahia.modules.googledriveprovider.flow;

import org.jahia.modules.external.admin.mount.AbstractMountPointFactory;
import org.jahia.services.content.JCRNodeWrapper;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * Created by david on 03/11/15.
 */
public class GoogleDriveProviderMountPointFactory extends AbstractMountPointFactory {

    private String name;

    private String localPath;

    private String root;

    public GoogleDriveProviderMountPointFactory() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    @Override
    public void populate(JCRNodeWrapper nodeWrapper) throws RepositoryException {
        super.populate(nodeWrapper);
        this.name = getName(nodeWrapper.getName());
        try {
            this.localPath = nodeWrapper.getProperty("mountPoint").getNode().getPath();
        }catch (PathNotFoundException e) {
            // no local path defined for this mount point
        }
        this.root = nodeWrapper.getPropertyAsString("j:rootPath");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLocalPath() {
        return localPath;
    }

    @Override
    public String getMountNodeType() {
        return "gdrive:mountPoint";
    }

    @Override
    public void setProperties(JCRNodeWrapper mountNode) throws RepositoryException {
        mountNode.setProperty("j:rootPath", root);
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
