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
package org.fs.ghanaian.json;

import org.fs.ghanaian.core.CoreCallback;
import org.fs.ghanaian.util.JsonUtility;
import org.fs.ghanaian.util.StringUtility;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Fatih on 6.10.2014.
 */
public abstract class JsonObjectCallback<T> extends CoreCallback<JSONObject> {

    //TODO this map needs to be expanded in recursive methodology, advanced mapping thoughts
    //--> a/b/c/1/d/c
    //--> a/b/c/d/c/e

    private Class<?> clazz;

    /***
     *
     * @param clazz
     */
    public JsonObjectCallback(Class<?> clazz) {
        this.clazz = clazz;
    }

    /***
     *
     * @param raw
     * @return
     */
    @Override
    public JSONObject parse(String raw) {
        return JsonUtility.parseObject(raw, null);
    }

    /***
     *
     * @param code
     * @param data
     */
    @Override
    public void result(int code, JSONObject data) {
        if(code == 200) {//this is fine if 201 or others get here we re doomed! may add parameter as expected code which is more RESt friendly.
            T dataSet = null;
            if(StringUtility.isNullOrEmpty(mMap)) {
                dataSet = marshall(data);
            } else {
                //Object dataInstance = JsonUtility.getJsonValue()
                JSONArray array = JsonUtility.getJsonArray(data, mMap, null);
                if(array != null) {
                    dataSet = marshall(array);
                } else {
                    JSONObject json = JsonUtility.getJsonObject(data, mMap, null);
                    dataSet = marshall(json);
                }
            }
            complete(dataSet, code);
        } else {
            complete(null, code);
        }
    }

    /**
     *
     * @param data
     * @return
     */
    public T marshall(JSONObject data) {
        try {
            Method method = clazz.getMethod("fromJsonObject", JSONObject.class);
            return (T)method.invoke(null, data);
        } catch (NoSuchMethodException ne) {
            ne.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        }
        return null;
    }

    /***
     *
     * @param data
     * @return
     */
    public T marshall(JSONArray data) {
        try {
            Method method = clazz.getMethod("fromJsonArray", JSONArray.class);
            return (T)method.invoke(null, data);
        } catch (NoSuchMethodException ne) {
            ne.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param dataSet
     * @param code
     */
    protected abstract void complete(T dataSet, int code);

    /***
     *
     * @return
     */
    @Override
    public boolean isLogEnabled() {
        return true;
    }

    /***
     *
     * @return
     */
    @Override
    public String getClassTag() {
        return JsonObjectCallback.class.getSimpleName();
    }
}
