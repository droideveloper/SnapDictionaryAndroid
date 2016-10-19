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
import org.json.JSONArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Fatih on 6.10.2014.
 */
public abstract class JsonArrayCallback<T> extends CoreCallback<JSONArray> {

    private Class<?> clazz;

    /**
     *
     * @param clazz
     */
    public JsonArrayCallback(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     *
     * @param raw
     * @return
     */
    @Override
    public JSONArray parse(String raw) {
        return JsonUtility.parseArray(raw, null);
    }

    /**
     *
     * @param code
     * @param data
     */
    @Override
    public void result(int code, JSONArray data) {
        if(code == 200) {
            T dataSet = marshall(data);
            complete(dataSet, code);
        } else {
            complete(null, code);
        }
    }

    /**
     *
     * @param array
     * @return
     */
    public T marshall(JSONArray array) {
        try {
            Method method = clazz.getMethod("fromArrayObject", JSONArray.class);
            return (T)method.invoke(null, array);
        } catch (NoSuchMethodException ne) {
            ne.printStackTrace();
        } catch (IllegalAccessException iea) {
            iea.printStackTrace();
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
        return JsonArrayCallback.class.getSimpleName();
    }
}
