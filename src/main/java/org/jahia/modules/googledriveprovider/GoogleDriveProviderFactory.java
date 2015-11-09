package org.jahia.modules.googledriveprovider;

import org.jahia.exceptions.JahiaInitializationException;
import org.jahia.modules.googledriveprovider.datasource.GoogleDriveDataSource;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.*;
import org.jahia.modules.external.ExternalContentStoreProvider;

import javax.jcr.RepositoryException;

/**
 * GoogleDrive mount point
 */
public class GoogleDriveProviderFactory implements ProviderFactory {
    @Override
    public String getNodeTypeName() {
        return "gdrive:mountPoint";
    }

    @Override
    public JCRStoreProvider mountProvider(JCRNodeWrapper mountPoint) throws RepositoryException {
        ExternalContentStoreProvider provider = (ExternalContentStoreProvider) SpringContextSingleton.getBean("ExternalStoreProviderPrototype");
        provider.setKey(mountPoint.getIdentifier());
        provider.setMountPoint(mountPoint.getPath());

        GoogleDriveDataSource dataSource = new GoogleDriveDataSource();

        // init data source with API KEY
        dataSource.init();

        provider.setDataSource(dataSource);
        provider.setDynamicallyMounted(true);
        provider.setSessionFactory(JCRSessionFactory.getInstance());

        try {
            provider.start();
        } catch (JahiaInitializationException e) {
            throw new RepositoryException(e);
        }
        return provider;
    }
}
