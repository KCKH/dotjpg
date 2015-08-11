/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.net.Uri;
import android.os.Bundle;

import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.ui.fragment.ImagesFragment;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 09/08/15.
 */
public class ImagesActivity extends BaseActivity {
    String mImagesType;
    String mGalleryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            Uri uri = getIntent().getData();
            if (uri != null) {
                U.l(uri.toString());
                mGalleryId = uri.toString().split("/")[4];
                if (U.getTrimmedString(mGalleryId).length() > 0) {
                    U.attachFragment(this, ImagesFragment.createInstance(Config.API_ACTION_GET_GALLERY, mGalleryId));
                }
            } else {
                mImagesType = data.getString(Config.EXTRA_IMAGES_TYPE, null);
                mGalleryId = data.getString(Config.EXTRA_GALLERY_ID, null);
                if (mImagesType != null) {
                    U.attachFragment(this, ImagesFragment.createInstance(mImagesType, mGalleryId));
                }
            }

            if (getSupportActionBar() != null) {
                if (mGalleryId != null) {
                    getSupportActionBar().setTitle(mGalleryId);
                }

                if (mImagesType != null && mImagesType.equals(Config.API_ACTION_GET_ALL_MY)) {
                    getSupportActionBar().setTitle(R.string.images_my);
                }
            }
        }
    }

    @Override
    protected boolean isAddImageButtonEnabled() {
        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected Boolean isChildActivity() {
        return true;
    }

    @Override
    protected String getActivityTag() {
        return Config.ACTIVITY_TAG_IMAGES + mImagesType;
    }
}
