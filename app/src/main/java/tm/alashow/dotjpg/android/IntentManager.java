/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.android;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.model.Image;
import tm.alashow.dotjpg.ui.activity.ImageDetailsActivity;
import tm.alashow.dotjpg.ui.activity.MainActivity;
import tm.alashow.dotjpg.ui.activity.NewImageActivity;
import tm.alashow.dotjpg.ui.activity.PreferencesActivity;
import tm.alashow.dotjpg.ui.activity.ViewImageActivity;

/**
 * Intent Manager for starting activities
 */
public class IntentManager {
    private Context mContext;

    private IntentManager(Context context) {
        this.mContext = context;
    }

    public static IntentManager with(Context _context) {
        return new Builder(_context).build();
    }

    public static class Builder {
        private final Context context;

        /**
         * Start building a new {@link IntentManager} instance.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context;
        }


        /**
         * Create the {@link IntentManager} instance.
         */
        public IntentManager build() {
            Context context = this.context;
            return new IntentManager(context);
        }
    }

    public void openMain() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        open(intent);
    }

    public void openImageDetails(String imageFilename, Image image) {
        Intent intent = new Intent(mContext, ImageDetailsActivity.class);
        if (imageFilename != null) {
            intent.putExtra(Config.EXTRA_IMAGE_FILENAME, imageFilename);
        }

        if (image != null) {
            intent.putExtra(Config.EXTRA_IMAGE, image);
        }

        open(intent);
    }

    public void openPreferences() {
        Intent intent = new Intent(mContext, PreferencesActivity.class);
        open(intent);
    }

    public void openImage(String url) {
        ArrayList<String> urls = new ArrayList<>();
        urls.add(url);
        openImages(urls, 0);
    }

    public void openImages(ArrayList<String> urls, int position) {
        Intent viewImageIntent = new Intent(mContext, ViewImageActivity.class);
        viewImageIntent.putExtra(Config.EXTRA_URLS, urls);
        viewImageIntent.putExtra(Config.EXTRA_POSITION, position);
        open(viewImageIntent);
    }

    public void newImage() {
        Intent intent = new Intent(mContext, NewImageActivity.class);
        open(intent);
    }

    /**
     * Starts given intent with builder config
     *
     * @param _intent which we want open
     */
    private void open(Intent _intent) {
        mContext.startActivity(_intent);
    }
}
