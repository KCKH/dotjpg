/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.os.Bundle;

import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.ui.fragment.ImagesFragment;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 30/07/15.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesManager.generateSessionIfEmpty();

        U.attachFragment(this, ImagesFragment.createInstance(Config.API_ACTION_GET_SPECIAL));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected Boolean isChildActivity() {
        return false;
    }

    @Override
    protected String getActivityTag() {
        return Config.ACTIVITY_TAG_MAIN;
    }

    @Override
    protected boolean isAddImageButtonEnabled() {
        return true;
    }
}
