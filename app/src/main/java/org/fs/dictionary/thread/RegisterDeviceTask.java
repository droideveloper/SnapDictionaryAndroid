package org.fs.dictionary.thread;

import android.content.Context;
import android.os.AsyncTask;

import org.fs.android.dictionary.backend.api.snapDictionaryApi.SnapDictionaryApi;
import org.fs.android.dictionary.backend.api.snapDictionaryApi.model.Device;
import org.fs.dictionary.SnapDictionaryApplication;
import org.fs.dictionary.utils.DeviceHelper;

import java.io.IOException;

/**
 * Created by Fatih on 01/12/14.
 */
public class RegisterDeviceTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public RegisterDeviceTask context(Context mContext) {
        this.mContext = mContext;
        return this;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SnapDictionaryApi snapDictionaryApi = SnapDictionaryApplication.snapDictionaryApi;
        if(snapDictionaryApi != null) {

            Device device = new Device();
            device.setAndroidId(DeviceHelper.getAndroidId(mContext));
            device.setAndroidOsVersion(DeviceHelper.getAndroidOsVersion());
            device.setProductName(DeviceHelper.getProductName());

            try {
                throw new IOException("");
                //snapDictionaryApi.registerDevice(device).execute();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //kinda release of strong reference
        if(mContext != null) {
            mContext = null;
        }
    }
}
