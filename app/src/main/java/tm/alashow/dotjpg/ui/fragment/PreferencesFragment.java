/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;

import tm.alashow.dotjpg.BuildConfig;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.android.PreferencesManager;


/**
 * Created by alashov on 18/01/15.
 */
public class PreferencesFragment extends android.support.v4.preference.PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        findPreference("about").setTitle("App Android v" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");

        findPreference("session_id").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.preferences_session)
                    .setMessage(getString(R.string.preferences_session_summary) + ":\n\n" + PreferencesManager.getInstance(getActivity()).getSessionId())
                    .show();
                return true;
            }
        });
    }
}
