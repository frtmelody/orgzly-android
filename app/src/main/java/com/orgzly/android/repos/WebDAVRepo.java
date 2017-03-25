package com.orgzly.android.repos;

import android.content.Context;
import android.net.Uri;

/**
 * Created by rafa on 25/03/17.
 */

public class WebDAVRepo implements Repo {

    private final Uri repoUri;
    private final WebDAVClient client;

    public WebDAVRepo(Context context, Uri uri){
        this.repoUri = uri;
        this.client = new WebDAVClient(context);
    }

    @Override
    Uri getUri() {
        return this.uri;
    }
}
