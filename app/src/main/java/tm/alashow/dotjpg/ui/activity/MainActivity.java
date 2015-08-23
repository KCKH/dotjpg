/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.ui.fragment.ImagesFragment;
import tm.alashow.dotjpg.util.ApiClient;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 30/07/15.
 */
public class MainActivity extends BaseActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging cloudMessaging;
    private String registrationId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesManager.generateSessionIfEmpty();

        U.attachFragment(this, ImagesFragment.createInstance(Config.API_ACTION_GET_SPECIAL));

        if (checkPlayServices()) {
            registerInBackground();
        }
    }


    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (cloudMessaging == null) {
                        cloudMessaging = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }
                    registrationId = cloudMessaging.register(Config.GCM_SENDER_ID);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (! TextUtils.isEmpty(registrationId)) {
                    U.l("Take this shit please, regId: " + registrationId);

                    RequestParams requestParams = new RequestParams();
                    requestParams.put(Config.API_CONTROLLER_PARAM, Config.API_CONTROLLER_USER);
                    requestParams.put(Config.API_ACTION_PARAM, Config.API_ACTION_REGISTER_USER);
                    requestParams.put(Config.API_SESSION_ID_PARAM, preferencesManager.getSessionId());
                    requestParams.put(Config.API_REGISTRATION_ID_PARAM, registrationId);

                    ApiClient.post(Config.API, requestParams, new JsonHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            U.l("failed to send regId: " + throwable.getLocalizedMessage());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            U.l("failed to send regId: " + throwable.getLocalizedMessage());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            U.l("failed to send regId: " + throwable.getLocalizedMessage());
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            U.l("RegId Sent! Server Response: " + response.toString());
                        }
                    });
                } else {
                    U.l("I don't know why, but i can't register you to google gcm");
                }
            }
        }.execute(null, null, null);
    }

    // Check if Google Play Services is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                U.l("Device has not google play services");
            }
            return false;
        }
        return true;
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
