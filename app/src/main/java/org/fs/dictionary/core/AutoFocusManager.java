package org.fs.dictionary.core;


import android.hardware.Camera;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Fatih on 23/11/14.
 */
public final class AutoFocusManager implements Camera.AutoFocusCallback {

    private Camera                  mCamera     = null;
    private AsyncTask<?, ?, ?>      mTask       = null;
    private boolean                 isFocusing  = false;
    private boolean                 isAvailable = false;

    private final static Collection<String> FOCUS_MODES;
    static {
        FOCUS_MODES = new ArrayList<String>(2);
        FOCUS_MODES.add(Camera.Parameters.FLASH_MODE_AUTO);
        FOCUS_MODES.add(Camera.Parameters.FOCUS_MODE_MACRO);
    }

    /**
     * Constructor
     * @param mCamera
     */
    public AutoFocusManager(Camera mCamera) {
        this.mCamera = mCamera;
        if(mCamera != null) {
            String currentMode = mCamera.getParameters().getFocusMode();
            isAvailable = FOCUS_MODES.contains(currentMode);
        }
    }

    /**
     * focus interface from camera.
     * @param done
     * @param camera
     */
    @Override
    public synchronized void onAutoFocus(boolean done, Camera camera) {
        isFocusing = false;
    }

    /**
     * job spot where we do the focus stuff.
     */
    private synchronized void run() {
        if(!isFocusing && mCamera != null) {
            try {
                mCamera.autoFocus(this);
                isFocusing = true;
            } catch (RuntimeException re) { re.printStackTrace(); }
        }
    }

    /**
     * trigger focus thread.
     */
    public void focus() {
        if(isAvailable) {
            mTask = new AutoFocusTask();
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * cancel focus thread if its alive.
     */
    public void cancel() {
        if(mTask != null) {
            mTask.cancel(true);
        }
        mTask = null;
    }

    /**
     * release resources
     */
    public void release() {
        cancel();
        if(mCamera != null) {
            mCamera = null;
        }
    }

    /**
     * Background thread
     */
    private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            try { Thread.sleep(100L);  } catch (InterruptedException ie) { ie.printStackTrace(); }
            run();
            return null;
        }
    }
}
