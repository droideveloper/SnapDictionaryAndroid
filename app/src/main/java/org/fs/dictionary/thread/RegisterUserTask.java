package org.fs.dictionary.thread;

import android.content.Context;
import android.os.AsyncTask;

import org.fs.android.dictionary.backend.api.snapDictionaryApi.SnapDictionaryApi;
import org.fs.android.dictionary.backend.api.snapDictionaryApi.model.User;
import org.fs.dictionary.SnapDictionaryApplication;
import org.fs.dictionary.utils.DeviceHelper;

import java.io.IOException;

/**
 * Created by Fatih on 01/12/14.
 */
public class RegisterUserTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public RegisterUserTask context(Context mContext) {
        this.mContext = mContext;
        return this;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SnapDictionaryApi snapDictionaryApi = SnapDictionaryApplication.snapDictionaryApi;
        if(snapDictionaryApi != null) {
            User user = new User();
            user.setName("Fatih Şen");
            user.setAbout("Application developer.");
            user.setBirtday("01/01/0001");
            user.setGender("male");
            user.setLocaltion("Istanbul");

            try {
                //snapDictionaryApi.signWith(DeviceHelper.getAndroidId(mContext), user).execute();
                throw new IOException("complete");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(mContext != null) {
            mContext = null;
        }
    }
}
