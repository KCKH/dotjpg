/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.model;

import org.json.JSONObject;

import java.io.Serializable;

import tm.alashow.dotjpg.util.DotjpgUtils;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 31/07/15.
 */
public class Image implements Serializable {
    private String imageFilename;
    private String imageId;
    private String galleryId;
    private String deleteToken;
    private long timestamp;
    private int width;
    private int height;

    public Image(JSONObject jsonObject) {
        try {
            this.imageFilename = jsonObject.getString("image");
            this.imageId = DotjpgUtils.trimImageExtension(imageFilename);

            this.timestamp = jsonObject.getLong("timestamp") * 1000;

            if (jsonObject.has("gallery")) {
                this.galleryId = jsonObject.getString("gallery");
            }

            if (jsonObject.has("delete_token")) {
                this.deleteToken = jsonObject.getString("delete_token");
            }

            if (jsonObject.has("height")) {
                this.width = jsonObject.getInt("width");
                this.height = jsonObject.getInt("height");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getImageId() {
        return imageId;
    }

    public Image setImageId(String imageId) {
        this.imageId = imageId;
        return this;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public Image setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
        return this;
    }

    public String getImageUrl() {
        return DotjpgUtils.getImageUrl(imageFilename);
    }

    public String getGalleryId() {
        return galleryId;
    }

    public Image setGalleryId(String galleryId) {
        this.galleryId = galleryId;
        return this;
    }

    public String getDeleteToken() {
        return deleteToken;
    }

    public Image setDeleteToken(String deleteToken) {
        this.deleteToken = deleteToken;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Image setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Image setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public boolean isGif() {
        return getImageFilename().endsWith(".gif");
    }

    public boolean hasDeleteToken() {
        return ! U.isStringEmpty(getDeleteToken());
    }

    public boolean hasGalleryId() {
        return ! U.isStringEmpty(getGalleryId());
    }
}
