/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg;

/**
 * Created by alashov on 16/01/15.
 */

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {
    public static volatile Context applicationContext = null;
    public static volatile Handler applicationHandler = null;
    private static volatile boolean applicationInited = false;

    public static void postInitApplication() {
        if (applicationInited) {
            return;
        }
        applicationInited = true;
        App app = (App) App.applicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        postInitApplication();
        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Shortcut for #applicationContext
     *
     * @return Context
     */
    public static Context c() {
        return applicationContext;
    }
}

