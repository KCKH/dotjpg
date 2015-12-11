/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.util;

import android.os.Build;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import tm.alashow.dotjpg.App;
import tm.alashow.dotjpg.BuildConfig;
import tm.alashow.dotjpg.Config;

public class ApiClient {
    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    public static int DEFAULT_TIMEOUT = 40 * 1000; //40 seconds

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        setUserAgent();
        get(url, params, responseHandler, DEFAULT_TIMEOUT);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, int timeoutInMilliSeconds) {
        setUserAgent();
        client.setTimeout(timeoutInMilliSeconds);
        client.get(App.applicationContext, getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        setUserAgent();
        post(url, params, responseHandler, DEFAULT_TIMEOUT);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, int timeoutInMilliSeconds) {
        setUserAgent();
        client.setTimeout(timeoutInMilliSeconds);
        client.post(App.applicationContext, getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return Config.SERVER + relativeUrl;
    }

    /**
     * Cancel all requests
     */
    public static void cancelAll() {
        try {
            client.cancelRequests(App.applicationContext, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUserAgent() {
        client.setUserAgent(Config.LOG_APP_NAME + "/" + BuildConfig.VERSION_NAME + " " + BuildConfig.VERSION_CODE + " (" + Build.MODEL + ", " + Build.VERSION.CODENAME + " " + Build.VERSION.SDK_INT + ")");
    }
}
