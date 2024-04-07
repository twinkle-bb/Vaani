package com.twink.vaani.repository;

import android.net.Uri;

import com.twink.vaani.model.ImageModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ImageRepo {
    long addImage(ImageModel image);

    List<ImageModel> getAllImages();

    void deleteImage(Long id);

    int update(Long id, String description);

    void importDatabase(InputStream inputStream) throws IOException;
}
