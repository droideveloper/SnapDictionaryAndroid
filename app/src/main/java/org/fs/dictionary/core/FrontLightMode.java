package org.fs.dictionary.core;

/**
 * Created by Fatih on 23/11/14.
 */
public enum FrontLightMode {
    ON,
    AUTO,
    OFF;

    /**
     * private accessor
     * @param value
     * @return
     */
    private static FrontLightMode parse(String value) {
        return value == null ? OFF : valueOf(value);
    }

    /**
     * gets user defined one or there is not defined one it will get as app defaults as OFF
     * @param mPreferanceManager
     * @return
     */
    public static FrontLightMode parse(PreferenceManager mPreferanceManager) {
        return parse(mPreferanceManager.getValueForKey(PreferenceManager.KEY_FRONT_LIGHT_MODE, OFF.toString()));
    }
}
