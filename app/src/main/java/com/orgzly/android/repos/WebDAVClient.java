package com.orgzly.android.repos;

import android.content.Context;
import android.net.Uri;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.orgzly.android.BookName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private Sardine client = SardineFactory.begin();
    private boolean tryLinking = false;

    public WebDAVClient(Context context) {
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
                    VersionedRook book = new VersionedRook(
                            repoUri,
                            uri,
                            res.toString(), //Revision
                            res.getModified().getTime()
                    );

                    list.add(book);
                }
            }
        } catch (IOException e){ //fix this.
        throw new IOException("Not a directory: " + repoUri);
    }

        return list;
    }

    public VersionedRook download(Uri repoUri, Uri uri, File file) {
        return null;
    }

    public VersionedRook upload(File file, Uri repoUri, String fileName) {
        return null;
    }

    public VersionedRook move(Uri repoUri, Uri fromUri, Uri toUri) {
        return null;
    }

    public void delete(String path) {
    }
}
