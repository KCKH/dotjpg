/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.util;

import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import tm.alashow.dotjpg.Config;

/**
 * Created by alashov on 02/08/15.
 */
public class DotjpgUtils {

    public static void loadImage(ImageView imageView, String imageUrl) {
        loadImage(imageView, Uri.parse(imageUrl), 1500, 1100);
    }

    public static void loadImage(ImageView imageView, Uri image, int maxWidth, int maxHeight) {

        int size = (int) Math.ceil(Math.sqrt(maxWidth * maxHeight));
        Picasso.with(imageView.getContext())
            .load(image)
            .transform(new BitmapTransform(maxWidth, maxHeight))
            .resize(size, size)
            .centerInside()
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
