package com.twink.vaani.utils;


import android.content.Context;
import android.net.Uri;

import java.io.File;

public class FileProvider extends androidx.core.content.FileProvider {

    public Uri getDatabaseURI(Context c) {

        File exportFile = c.getDatabasePath(Constant.DB_NAME); // new approach

        return getFileUri(c, exportFile);
    }

    public Uri getFileUri(Context c, File f) {
        return getUriForFile(c, Constant.FILE_AUTHORITY, f);
    }

}