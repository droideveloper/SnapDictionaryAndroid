package org.fs.dictionary.core;

import android.net.Uri;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.fs.dictionary.SnapDictionaryApplication;

import java.io.IOException;

/**
 * Created by Fatih on 02/12/14.
 */
public abstract class MediaDownloadManager implements Callback {

    private final static OkHttpClient client = new OkHttpClient();

    private Request request;

    public MediaDownloadManager addRequest(Request request) {
        this.request = request;
        return this;
    }

    public void consume() {
        if(request != null) {
            client.newCall(request)
                  .enqueue(this);
        } else {
            onDownload(null);
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        e.printStackTrace();
        onDownload(null);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if(response.code() == 200) {
            String url = SnapDictionaryApplication.mFileManager.writeMediaFile(response.body().byteStream());
            onDownload(Uri.parse(url));
            return;
        }
        onDownload(null);
    }

    public abstract void onDownload(Uri uri);
}
