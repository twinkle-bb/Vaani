package com.twink.vaani.repository;

import com.twink.vaani.model.ImageModel;

import java.util.List;

public interface ImageRepo {
    long addImage(ImageModel image);
    List<ImageModel> getAllImages();

    void deleteImage(Long id);

    int update(Long id,String description);
}
