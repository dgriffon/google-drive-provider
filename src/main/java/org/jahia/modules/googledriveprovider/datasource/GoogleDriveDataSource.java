package org.jahia.modules.googledriveprovider.datasource;


import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.model.FileList;
import org.apache.commons.io.IOUtils;
import org.jahia.modules.external.FilesDataSource;

import javax.jcr.Binary;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.jahia.modules.googledriveprovider.drive.DriveQuickstart;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by david on 26/10/15.
 */


public class GoogleDriveDataSource extends FilesDataSource {

    private static  Drive drive;
    private static final String APPLICATION_NAME = "Your app name";
    private static final JacksonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT =
            new NetHttpTransport();

    // ...


    public void init() {
        try {
            // drive = buildService(DriveAccess.getCredentials());
            drive = DriveQuickstart.getDriveService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExternalFile getExternalFile(String path) throws PathNotFoundException {
        if ("/".equals(path)) {
            return new ExternalFile(ExternalFile.FileType.FOLDER, path, null, null);
        }
        try {
            File f = drive.files().get(path.substring(1)).execute();
            ExternalFile ef = new ExternalFile(ExternalFile.FileType.FILE,"/" + f.getId() ,f.getModifiedByMeDate()!=null?new Date(f.getModifiedByMeDate().getValue()):null, new Date(f.getCreatedDate().getValue()));
            ef.getProperties().put("jcr:title", new String[] {f.getTitle()});
            ef.setContentType(f.getMimeType());
            return ef;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ExternalFile> getChildrenFiles(String path) throws RepositoryException {
        try {
            return retrieveAllFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Binary getFileBinary(final String path) throws PathNotFoundException {
        return new Binary() {

            private InputStream is;

            @Override
            public InputStream getStream() throws RepositoryException {
                if (is == null) {
                    try {
                        if (drive.files().get(path.substring(1)).execute().getMimeType().equals("application/vnd.google-apps.spreadsheet")) {
                            is = new FileInputStream("test");
                        } else {
                            is = drive.files().get(path.substring(1)).executeMediaAsInputStream();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ;
                }
                return is;
            }

            @Override
            public int read(byte[] bytes, long l) throws IOException, RepositoryException {
                InputStream is = null;
                int read = 0;
                try {
                    is = getStream();
                    read = is.read(bytes, (int) l, bytes.length);
                } finally {
                    IOUtils.closeQuietly(is);
                }
                return read;
            }

            @Override
            public long getSize() throws RepositoryException {
                try {
                    return drive.files().get(path.substring(1)).execute().getFileSize();
                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            }

            @Override
            public void dispose() {
                if (is != null) {
                    IOUtils.closeQuietly(is);
                }
            }
        };
    }

    /**
     * Retrieve a list of File resources.
     *
     * @return List of File resources.
     */
    private List<ExternalFile> retrieveAllFiles() throws IOException {
        List<ExternalFile> result = new ArrayList<ExternalFile>();
        Drive.Files.List request = drive.files().list();

        do {
            try {
                FileList files = request.execute();
                for (File f : files.getItems()) {
                    ExternalFile ef = new ExternalFile(ExternalFile.FileType.FILE,"/" + f.getTitle() ,f.getModifiedByMeDate()!=null?new Date(f.getModifiedByMeDate().getValue()):null, new Date(f.getCreatedDate().getValue()));
                    if (f.getFileSize() != null) {
                        ef.getProperties().put("jcr:title", new String[] {f.getTitle()});
                        result.add(ef);
                    }
                }
                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (result.size() < 20 && request.getPageToken() != null &&
                request.getPageToken().length() > 0);

        return result;
    }

}
