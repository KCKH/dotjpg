/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.android;

import android.content.Context;
import android.content.SharedPreferences;

import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 20/12/14.
 */
public class PreferencesManager {

    private Context mContext;
    private final String PREFERENCES_NAME = "tm.alashow.dotjpg_preferences";
    public static final String KEY_SESSION_ID = "session_id";

    private static PreferencesManager instance = null;

    /**
     * @param context App Context
     */
    protected PreferencesManager(Context context) {
        mContext = context;
    }

    // Lazy Initialization (If required then only)
    public static PreferencesManager getInstance(Context context) {
        if (context == null) {
            throw new IllegalStateException("Context must not be null");
        }
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (PreferencesManager.class) {
                if (instance == null) {
                    instance = new PreferencesManager(context);
                }
            }
        }
        return instance;
    }

    public String getSessionId() {
        return getPreferences().getString(KEY_SESSION_ID, null);
    }

    public void setSessionId(String value) {
        getPreferences().edit().putString(KEY_SESSION_ID, value).apply();
    }

    public void generateSessionIfEmpty() {
        if (getSessionId() == null) {
            setSessionId(U.randomSessionId());
        }
    }

    public String getString(String key) {
        return getPreferences().getString(key, "");
    }

    public void setString(String key, String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    /**
     * @return {@link SharedPreferences}
     */
    public SharedPreferences getPreferences() {
        if (this.mContext != null) {
            return this.mContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        } else {
            return null;
        }
    }
}
