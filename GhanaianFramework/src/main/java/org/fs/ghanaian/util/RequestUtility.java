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

import android.net.Uri;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.Map;

/**
 * Created by Fatih on 6.10.2014.
 */
public class RequestUtility {

    /***
     * Creates POST Request object from given url and parameters
     *
     * @param baseUrl  --> BASE_URL string
     * @param params   --> PARAMETER map<string, string>
     * @param rawPost  --> Raw post body string
     * @param mime     --> Mime type of request body (text/html, application/json) its better to use charset=utf-8 too!
     * @return --> POST Request object
     */
    public static Request buildPost(String baseUrl, Map<String, String> params, String rawPost, String mime) {
        RequestBody body = body(rawPost, mime);
        return buildGet(baseUrl, params).newBuilder()
                                        .post(body)
                                        .build();
    }

    /***
     * Creates PUT Request object from given url and parameters
     *
     * @param baseUrl  --> BASE_URL string
     * @param params   --> PARAMETER map<string, string>
     * @param rawPut   --> Raw put body string
     * @param mime     --> Mime type of request body (text/html, application/json) its better to use charset=utf-8 too!
     * @return --> PUT Request object
     */
    public static Request buildPut(String baseUrl, Map<String, String> params, String rawPut, String mime) {
        RequestBody body = body(rawPut, mime);
        return buildGet(baseUrl, params).newBuilder()
                                        .put(body)
                                        .build();
    }


    /***
     * Creates PATCH Request object from given url and parameters
     *
     * @param baseUrl  --> BASE_URL string
     * @param params   --> PARAMETER map<string, string>
     * @param rawPatch --> Raw patch body string
     * @param mime     --> Mime type of request body (text/html, application/json) its better to use charset=utf-8 too!
     * @return --> PATCH Request object
     */
    public static Request buildPatch(String baseUrl, Map<String, String> params, String rawPatch, String mime) {
        RequestBody body = body(rawPatch, mime);
        return buildGet(baseUrl, params).newBuilder()
                                        .patch(body)
                                        .build();
    }

    /***
     * Creates DELETE Request object from given url and parameters
     *
     * @param baseUrl --> BASE_URL string
     * @param params  --> PARAMETER map<string, string>
     * @return --> DELETE Request object
     */
    public static Request buildDelete(String baseUrl, Map<String, String> params) {
        String url = build(baseUrl, params);
        if(!StringUtility.isNullOrEmpty(url)) {
            return new Request.Builder()
                    .delete()
                    .url(url)
                    .build();
        }
        return null;
    }

    /***
     * Creates GET Request object from given url and parameters
     *
     * @param baseUrl --> BASE_URL string
     * @param params  --> PARAMETER map<string, string>
     * @return --> GET Request object
     */
    public static Request buildGet(String baseUrl, Map<String, String> params) {
        String url = build(baseUrl, params);
        if(!StringUtility.isNullOrEmpty(url)) {
            return new Request.Builder()
                              .url(url)
                              .build();
        }
        return null;
    }

    /**
     * Builds end url from base url and map object provided
     *
     * @param baseUrl --> BASE_URL string
     * @param params  --> PARAMETER map<string, string>
     * @return Build String url
     */
    static String build(String baseUrl, Map<String, String> params) {
        if(params != null) {
            if(!StringUtility.isNullOrEmpty(baseUrl)) {
                Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
                for(String key : params.keySet()) {
                    String value = params.get(key);
                    if(StringUtility.isNullOrEmpty(value)) {
                        builder = addPathParameter(builder, key);
                    } else {
                        builder = addQueryParamater(builder, key, value);
                    }
                }
                return builder.build()
                        .toString();
            }
        }
        return baseUrl;
    }

    /**
     * Adds Path parameters used.
     *
     * @param builder
     * @param key
     * @return
     */
    static Uri.Builder addPathParameter(Uri.Builder builder, String key) {
        if(builder != null) {
            builder.appendPath(key);
            return builder;
        }
        return builder;
    }

    /***
     *Adds Query parameters used.
     *
     * @param builder
     * @param key
     * @param value
     * @return
     */
    static Uri.Builder addQueryParamater(Uri.Builder builder, String key, String value) {
        if(builder != null) {
            builder.appendQueryParameter(key, value);
            return builder;
        }
        return null;
    }

    /***
     * Converts rawRequestString and Mime to RequestBody object.
     * dont forget to pass charset as charset=utf-8
     *
     * @param rawPost
     * @param mime
     * @return
     */
    static RequestBody body(String rawPost, String mime) {
        return RequestBody.create(MediaType.parse(mime), rawPost);
    }
}
