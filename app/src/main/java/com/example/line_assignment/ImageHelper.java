package com.example.line_assignment;

public class ImageHelper {

    private String imageId;
    private byte[] imageByteArray;

    public ImageHelper(String imageId, byte[] imageByteArray) {
        this.imageId = imageId;
        this.imageByteArray = imageByteArray;
    }

    public ImageHelper() {
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }
}
