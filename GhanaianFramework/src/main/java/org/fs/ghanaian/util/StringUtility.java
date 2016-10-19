/*
Copyright 2014 GhanaianFramework.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.fs.ghanaian.util;

import android.text.TextUtils;
import android.util.Base64;
import org.json.JSONArray;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatih on 6.10.2014.
 */
public class StringUtility
{
    private final static String KEY_ENCODING                = "UTF-8";
    private final static String KEY_HASH_SHA256             = "HmacSHA256";
    private final static String KEY_HASH_SHA1               = "HmacSHA1";
    /**
     *
     * @param value is the value for control.
     * @param <T> is any object type to check, if String is the super class it will check for empty too.
     * @return
     */
    public static <T> boolean isNullOrEmpty(T value) {
        if(value == null) {
            return true;
        }
        if(value instanceof String) {
            String val = (String)value;
            if(TextUtils.isEmpty(val)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getStringList(JSONArray array)  {
        List<String> values = null;
        if(array != null) {
            int size = array.length();
            values = new ArrayList<String>(size);
            for(int i = 0; i < size; i++) {
                String value = JsonUtility.getArrayString(array, i, null);
                if(!isNullOrEmpty(value)) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    public static  String sha1(String s, String keyString)	throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = getMac(keyString, KEY_HASH_SHA1);
        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
        return new String(Base64.encodeToString(bytes, 0));
    }

    public static String sha256(String data, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] hmacValue = null;
        Mac mac = getMac(key, KEY_HASH_SHA256);
        hmacValue = mac.doFinal(data.getBytes(KEY_ENCODING));
        return new String(Base64.encodeToString(hmacValue, 0));
    }

    static Mac getMac(String key, String algo) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(KEY_ENCODING), algo);
        Mac mac = Mac.getInstance(algo);
        mac.init(secretKeySpec);
        return mac;
    }
}
