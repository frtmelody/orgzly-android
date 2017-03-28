package com.orgzly.android.repos;

import android.content.Context;
import android.net.Uri;

import com.fasterxml.jackson.core.Versioned;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;
import com.orgzly.android.BookName;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by @rafaelleru on 25/03/17.
 */

public class WebDAVClient {
    private static final String TAG = WebDAVClient.class.getName();
    private static final long UPLOAD_FILE_SIZE_LIMIT = 150; // MB

    private static final String NOT_LINKED = "Not linked to Dropbox";
    private static final String LARGE_FILE = "File larger then " + UPLOAD_FILE_SIZE_LIMIT + " MB";

    private Context context;
    private Sardine client;
    private boolean tryLinking = false;

    public WebDAVClient(Context context) {
        this.client = SardineFactory.begin();
        this.context = context;
    }

    public boolean isLinked(){
        return this.client == null;
    }

    public List<VersionedRook> getBooks(Uri repoUri) throws IOException {
        List<VersionedRook> list = new ArrayList<>();

        try {
            String path = repoUri.getPath();
            if (path == null) {
                path = "/";
            }

            List<DavResource> resources = this.client.list(path);

            for (DavResource res : resources) {
                // System.out.println(res); // calls the .toString() method.
                if (BookName.isSupportedFormatFileName(res.getName())) {
                    Uri uri = repoUri.buildUpon().appendPath(res.getPath()).build();
                    String rev = res.getModified().toString();
                    long last_modified = res.getModified().getTime();
                    VersionedRook book = new VersionedRook(
                            repoUri,
                            uri,
                            rev,
                            last_modified
                    );

                    list.add(book);
                }
            }
        } catch (IOException e){ //fix this.
        throw new IOException("Not a directory: " + repoUri);
    }

        return list;
    }

    /** Download a file from webDAV server */

    public VersionedRook download(Uri repoUri, Uri uri, File localFile) throws IOException {
        if(!isLinked()){
            throw new IOException(NOT_LINKED);
        }

        if (client.exists(uri.getPath())) {
            InputStream is = client.get(repoUri.getPath());
            String path = localFile.getPath();

            byte[] file = new byte[is.available()];
            is.read(file);

            OutputStream ou = new FileOutputStream(localFile);
            ou.write(file);

            String rev = client.list(uri.getPath()).get(0).getModified().toString();
            long last_modified = client.list(repoUri.getPath()).get(0).getModified().getTime();

            is.close();
            ou.close();

            return new VersionedRook(repoUri, uri, rev, last_modified);
        } else {
            throw new IOException("Failed downloading webDAV file " + uri + ": Not a file");
        }

    }

    public VersionedRook upload(File file, Uri repoUri, String fileName) throws IOException {
        Uri bookUri = repoUri.buildUpon().appendPath(fileName).build();

        InputStream is = new FileInputStream(file);
        byte [] file_data = new byte[is.available()];
        is.read(file_data);

        client.put(repoUri.getPath() + fileName, file_data);

        String rev = client.list(repoUri.getPath() + fileName).get(0).getModified().toString();
        long last_modified = client.list(repoUri.getPath() + fileName).get(0).getModified().getTime();

        return new VersionedRook(repoUri, bookUri, rev, last_modified);

    }

    public VersionedRook move(Uri repoUri, Uri fromUri, Uri toUri) throws IOException {

        client.move(fromUri.getPath(), toUri.getPath());

        String rev = client.list(toUri.getPath()).get(0).getModified().toString();
        long last_modified = client.list(toUri.getPath()).get(0).getModified().getTime();

        return new VersionedRook(repoUri, toUri, rev, last_modified);
    }

    public void delete(String path) {
    }
}
