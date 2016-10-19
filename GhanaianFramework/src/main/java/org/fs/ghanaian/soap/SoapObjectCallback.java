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
package org.fs.ghanaian.soap;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.fs.ghanaian.core.CoreCallback;
import org.fs.ghanaian.util.StringUtility;
import org.fs.ghanaian.xml.DocumentHelper;
import org.fs.ghanaian.xml.Serializer;
import org.w3c.dom.Document;

/**
 * Created by Fatih on 22/10/14.
 */
public abstract class SoapObjectCallback<T> extends CoreCallback<Document> {

    private Class<?> clazz;
    private Envelope envelope;

    /**
     *
     * @param clazz
     */
    public SoapObjectCallback(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     *
     * @param envelope
     * @return
     */
    public SoapObjectCallback addEnvelope(Envelope envelope) {
        this.envelope = envelope;
        return this;
    }

    /**
     *
     * @param raw
     * @return
     */
    @Override
    public Document parse(String raw) {
        return DocumentHelper.create(raw);
    }

    /***
     *
     * @param request
     * @return
     */
    @Override
    public CoreCallback<Document> addRequest(Request request) {
        if(request != null) {
            Serializer serializer = new Serializer();
            RequestBody body = RequestBody.create(MediaType.parse("text/xml; charset=utf-8"), serializer.serialize(envelope));
            Request newRequest = request.newBuilder()
                    .post(body)
                    .build();
            mRequest = newRequest;
        }
        return this;
    }

    /**
     *
     * @param code
     * @param data
     */
    @Override
    public void result(int code, Document data) {
        if(code == 200) {
            T dataSet = null;
            mMap = mMap == null ? clazz.getSimpleName() : mMap;//its kinda weird to have this style but not sure.
            if(StringUtility.isNullOrEmpty(mMap)) {
                dataSet = marshall(data);
            } else {
                dataSet = marshall(data, mMap);
            }
            complete(dataSet, code);
        } else {
            complete(null, code);
        }
    }

    /**
     *
     * @param document
     * @return
     */
    public T marshall(Document document) {
        Serializer serializer = new Serializer();
        return serializer.deserialize(document, clazz);
    }

    /**
     *
     * @param document
     * @param tagStart
     * @return
     */
    public T marshall(Document document, String tagStart) {
        Serializer serializer = new Serializer();
        return serializer.deserialize(document, clazz, tagStart);
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
        return SoapObjectCallback.class.getSimpleName();
    }
}
