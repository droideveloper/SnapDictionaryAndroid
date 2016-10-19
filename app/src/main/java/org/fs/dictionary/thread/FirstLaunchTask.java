package org.fs.dictionary.thread;


import android.content.res.AssetManager;
import android.os.AsyncTask;

import org.fs.dictionary.SnapDictionaryApplication;
import org.fs.dictionary.core.PreferenceManager;

import java.io.IOException;

/**
 * Created by Fatih on 27/11/14.
 */
public class FirstLaunchTask extends AsyncTask<Void, Void, Void> {

   private AssetManager mAssetManager;

   public FirstLaunchTask setAssetManager(AssetManager mAssetManager) {
       this.mAssetManager = mAssetManager;
       return this;
   }

   @Override
   protected Void doInBackground(Void... voids) {
       try {
           SnapDictionaryApplication.mFileManager.setAssetManager(mAssetManager);
           SnapDictionaryApplication.mPreferenceManager.setValueForKey(PreferenceManager.KEY_FIRST_LAUNCH, false);
       } catch (IOException ioe) {
           ioe.printStackTrace();
           return null;
       }
       return null;
   }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mAssetManager = null;
    }
}
