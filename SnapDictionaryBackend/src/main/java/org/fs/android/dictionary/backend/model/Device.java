package org.fs.android.dictionary.backend.model;

import com.google.appengine.api.datastore.Key;

/**
 * Created by Fatih on 30/11/14.
 */
public class Device {

    private String androidId;

    private String androidOsVersion;

    private String productName;

    private Key key;

    private String guild;//assigned in the next level of log in server creates this.

    /* Setters and Getters */

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getAndroidOsVersion() {
        return androidOsVersion;
    }

    public void setAndroidOsVersion(String androidOsVersion) {
        this.androidOsVersion = androidOsVersion;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getGuild() {
        return guild;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}
