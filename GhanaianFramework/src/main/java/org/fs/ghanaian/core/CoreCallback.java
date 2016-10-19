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
package org.fs.ghanaian.core;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.fs.ghanaian.util.StringUtility;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * Created by Fatih on 6.10.2014.
 */
public abstract class CoreCallback<T> extends CoreObject implements Callback {

    private final static OkHttpClient mClient = new OkHttpClient();
    protected Request mRequest = null;
    protected String mMap = null;

    /***
     *
     * @param request
     * @return
     */
    public CoreCallback<T> addRequest(Request request) {
        this.mRequest = request;
        return this;
    }

    /**
     *
     * @param map
     * @return
     */
    public CoreCallback<T> map(String map) {
        this.mMap = map;
        return this;
    }

    /***
     *
     */
    public void consume() {
        if(mRequest == null) {
            result(-1, null);
        } else {
            mClient.newCall(mRequest)
                   .enqueue(this);
        }
    }

    /***
     *
     * @param request
     * @param e
     */
    @Override
    public void onFailure(Request request, IOException e) {
        if(e != null) {
            StringWriter w = new StringWriter();
            PrintWriter p = new PrintWriter(w);
            e.printStackTrace(p);
            log(Log.WARN, w.toString());
        }
    }

    /***
     *
     * @param response
     * @throws IOException
     */
    @Override
    public void onResponse(Response response) throws IOException {
        int httpCode = response.code();

        if(isLogEnabled()) {
            Headers h = response.headers();
            Set<String> keys = h.names();
            for (String key : keys) {
                Log.println(Log.ERROR, CoreCallback.class.getSimpleName(), "******** start *******");
                Log.println(Log.ERROR, CoreCallback.class.getSimpleName(), key);
                List<String> values = h.values(key);
                for (String value : values) {
                    Log.println(Log.ERROR, CoreCallback.class.getSimpleName(), value);
                }
                Log.println(Log.ERROR, CoreCallback.class.getSimpleName(), "******** end *******");
            }
        }

        if(httpCode == 200) {
            if(response != null) {
                ResponseBody body = response.body();
                if(body != null) {
                    String raw = body.string();
                    if(!StringUtility.isNullOrEmpty(raw)) {
                        T object = parse(raw);
                        result(httpCode, object);
                        return;
                    }
                }
            }
        }
        result(httpCode, null);
    }

    /***
     *
     * @param raw
     * @return
     */
    public abstract T parse(String raw);

    /***
     *
     * @param code
     * @param data
     */
    public abstract void result(int code, T data);
}
