package org.fs.dictionary.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Fatih on 23/11/14.
 */
public final class PreferenceManager {

    private final static String KEY_PREFERENCE_NAME = "snap.dict.user.preference";

    public final static String KEY_FIRST_LAUNCH    = "snap.dict.user.first.launch";


    public final static String KEY_FRONT_LIGHT_MODE = "user.pref.front.light.mode";

    public final static String KEY_AUTO_FOCUS_MODE  = "user.pref.auto.focus.mode";
    public final static String KEY_DISABLE_EXPOSURE = "user.pref.disable.exposure";
    public final static String KEY_DISABLE_METERING = "user.pref.disable.metering";

    public final static String KEY_LANGUAGE_PREFERENCE = "user.prefs.language";
    public final static String KEY_ZOOM_LEVEL          = "user.prefs.zoom";
    public final static String KEY_FLASH_MODE          = "user.prefs.flash.mode";


    private Context mContext;
    private SharedPreferences mSharedPreferences;


    public PreferenceManager(Context mContext) {
        this.mContext = mContext;
        if(mContext == null) {
            throw new NullPointerException("we need context instance for initialization or reading preferences.");
        }
        initWithContext();
        //first launch or not check
        if(!isKey(KEY_FIRST_LAUNCH)) {
            //this is first launch so need to create it and set it true or false
            setValueForKey(KEY_FIRST_LAUNCH, true);
            //init with defaults
            initWithContext();
        }
    }

    /**
     * initializer
     */
    private void initWithContext() {
        mSharedPreferences = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);//always private
    }

    private void initWithDefaults() {
        //initialize default values.
    }

    /**
     * contains key or not
     * @param key
     * @return
     */
    public boolean isKey(String key) {
        if(mSharedPreferences == null) {
            throw new NullPointerException("context and rest is destroyed");
        }
        return mSharedPreferences.contains(key);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getValueForKey(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Boolean getValueForKey(String key, Boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Integer getValueForKey(String key, Integer defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Float getValueForKey(String key, Float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Long getValueForKey(String key, Long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValueForKey(String key, String value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValueForKey(String key, Boolean value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValueForKey(String key, Integer value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValueForKey(String key, Float value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValueForKey(String key, Long value) {
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     *
     * @return
     */
    private SharedPreferences.Editor getEditor() {
        return mSharedPreferences.edit();
    }

    /**
     * Release resources
     */
    public void release() {
        if(mSharedPreferences != null) {
            mSharedPreferences = null;
        }

        if(mContext != null) {
            mContext = null;
        }
    }
}
