package org.fs.dictionary.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import org.fs.ghanaian.util.StringUtility;

/**
 * Created by Fatih on 29/11/14.
 */
public class DeviceHelper {

    private final static String SECRET_KEY = "2a1f65bf-0686-4f31-8d55-984de4b8db33";

    /**
     * Encoded Android ID retrieved from context
     * @param mContext context to get Android id with it
     * @return encoded android id value
     */
    public static String getAndroidId(Context mContext) {
       return encodeWith(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
    }

    /**
     * name and the level index.
     * @return
     */
    public static String getAndroidOsVersion() {
        return String.format("Android %s ( %d API )",Build.VERSION.CODENAME, Build.VERSION.SDK_INT);
    }

    /**
     * Device model, product.
     * @return
     */
    public static String getProductName() {
        return String.format("%s, %s by %s", Build.MANUFACTURER, Build.MODEL, Build.BRAND);
    }

    /**
     *
     * @param data --> mostly we used this to be decoded with the android id property not to be stolen from other people
     * @return
     */
    private static String encodeWith(String data) {
        try {
            return StringUtility.sha256(data, SECRET_KEY);
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
}
