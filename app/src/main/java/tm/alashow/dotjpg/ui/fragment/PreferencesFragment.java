/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.fragment;

import android.os.Bundle;

import tm.alashow.dotjpg.BuildConfig;
import tm.alashow.dotjpg.R;


/**
 * Created by alashov on 18/01/15.
 */
public class PreferencesFragment extends android.support.v4.preference.PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        findPreference("about").setTitle("App Android v" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");
    }
}
