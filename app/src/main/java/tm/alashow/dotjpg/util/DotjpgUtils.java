/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.util;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import tm.alashow.dotjpg.Config;

/**
 * Created by alashov on 02/08/15.
 */
public class DotjpgUtils {

    public static void loadImage(ImageView imageView, Uri image) {
        Glide.with(imageView.getContext())
            .load(image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .placeholder(U.imagePlaceholder())
            .into(imageView);
    }

    public static String getImageUrl(String imageId) {
        return Config.SERVER + imageId;
    }

    public static String getImagePageUrl(String imageId) {
        return Config.SERVER + "i/" + imageId;
    }

    public static String getImageDeleteUrl(String deleteToken) {
        return Config.SERVER + "delete/" + deleteToken;
    }

    public static String getImageHtmlMarkup(String imageId) {
        return "<a href=\"" + getImagePageUrl(imageId) + "\"><img src=\"" + getImageUrl(imageId) + "\" /></a>";
    }

    public static String getImageMarkdownMarkup(String imageId) {
        return "[![image](" + getImageUrl(imageId) + ")](" + getImagePageUrl(imageId) + ")";
    }

    public static String getImageBbMarkup(String imageId) {
        return "[url=" + getImagePageUrl(imageId) + "][img]" + getImageUrl(imageId) + "[/img][/url]";
    }

    public static String getImageGalleryUrl(String galleryId) {
        return Config.SERVER + "g/" + galleryId;
    }

    /**
     * Trim image filename and get imageId
     *
     * @param imageFilename full image name
     * @return imageId
     */
    public static String trimImageExtension(String imageFilename) {
        return imageFilename.split("\\.")[0];
    }
}
