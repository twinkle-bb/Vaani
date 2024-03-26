package com.twink.vaani.model;

public class ImageModel {
    private String img_desc;
    private byte[] image;

    private long id;

    public ImageModel(String img_desc, byte[] image, long id) {
        this.img_desc = img_desc;
        this.image = image;
        this.id = id;
    }

    public ImageModel() {

    }

    public String getImg_desc() {
        return img_desc;
    }

    public void setImg_desc(String img_desc) {
        this.img_desc = img_desc;
    }


    public long getId() {
        return id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "img_desc='" + img_desc + '\'' +
                ", image='" + image + '\'' +
                ", id=" + id +
                '}';
    }
}
