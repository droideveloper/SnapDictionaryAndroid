package org.fs.dictionary;
import android.os.AsyncTask;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.fs.android.dictionary.backend.api.snapDictionaryApi.SnapDictionaryApi;
import org.fs.dictionary.core.CoreApplication;
import org.fs.dictionary.core.FileManager;
import org.fs.dictionary.thread.FirstLaunchTask;
import org.fs.dictionary.core.PreferenceManager;
import org.fs.dictionary.utils.LogHelper;

import java.io.IOException;


/**
 * Created by Fatih on 23/11/14.
 */
public class SnapDictionaryApplication extends CoreApplication {

    public static PreferenceManager mPreferenceManager = null;
    public static FileManager       mFileManager       = null;

    public static SnapDictionaryApi  snapDictionaryApi = null;


    private LogHelper mLogHelper = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mLogHelper = new LogHelper(this);
        mPreferenceManager = new PreferenceManager(getApplicationContext());
        mFileManager = new FileManager(getApplicationContext());

        //copying .traineddata extension for O.C.R. libraries use for detecting words.
        boolean isFirstLaunch = mPreferenceManager.getValueForKey(PreferenceManager.KEY_FIRST_LAUNCH, false);
        if(isFirstLaunch) {
            new FirstLaunchTask()
                    .setAssetManager(getAssets())
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
        }

        //initialize google cloud endpoint api client reference singleton.
        if(snapDictionaryApi == null) {
            SnapDictionaryApi.Builder builder = new SnapDictionaryApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("http://10.0.3.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            snapDictionaryApi = builder.build();
        }
    }

    public boolean isLogEnabled() {
        return true;
    }

    public String getClassTag() {
        return SnapDictionaryApplication.class.getSimpleName();
    }
}
