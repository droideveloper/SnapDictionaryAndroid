package org.fs.dictionary.core;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;


import java.io.IOException;

/**
 * Created by Fatih on 23/11/14.
 */
public final class CameraManager {

    private final CameraConfigurationManager        mConfigManager;
    private Camera                                  mCamera;
    private AutoFocusManager                        mAutoFocusManager;
    private Rect                                    mFramingRect;
    private Rect                                    mFramingRectInPreview;
    private boolean                                 mInitialized;
    private boolean                                 mPreviewing;
    private int                                     mRequestedCameraId = -1;
    private int                                     mRequestedFramingRectWidth;
    private int                                     mRequestedFramingRectHeight;

    private PreferenceManager                       mPreferanceManager;
    /**
     * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
     * clear the handler so it will only receive one message.
     */

    public CameraManager(Context mContext, PreferenceManager mPreferenceManager) {
        this.mPreferanceManager = mPreferenceManager;
        this.mConfigManager = new CameraConfigurationManager(mContext, mPreferenceManager);
    }

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the camera will draw preview frames into.
     * @throws IOException Indicates the camera driver failed to open.
     */
    public synchronized void openDriver(SurfaceHolder holder) throws IOException {
        Camera theCamera = mCamera;
        if (theCamera == null) {
            if (mRequestedCameraId >= 0) {
                theCamera = OpenCamera.open(mRequestedCameraId);
            } else {
                theCamera = OpenCamera.open();
            }

            if (theCamera == null) {
                throw new IOException();
            }
            mCamera = theCamera;
        }
        theCamera.setPreviewDisplay(holder);

        if (!mInitialized) {
            mInitialized = true;
            mConfigManager.initFromCameraParameters(theCamera);
            if (mRequestedFramingRectWidth > 0 && mRequestedFramingRectHeight > 0) {
                setManualFramingRect(mRequestedFramingRectWidth, mRequestedFramingRectHeight);
                mRequestedFramingRectWidth = 0;
                mRequestedFramingRectHeight = 0;
            }
        }

        Camera.Parameters parameters = theCamera.getParameters();
        String parametersFlattened = parameters == null ? null : parameters.flatten(); // Save these, temporarily
        try {
            mConfigManager.setDesiredCameraParameters(theCamera, false);
            mConfigManager.setPhotoResolution(theCamera);
        } catch (RuntimeException re) {

            if (parametersFlattened != null) {
                parameters = theCamera.getParameters();
                parameters.unflatten(parametersFlattened);
                try {
                    theCamera.setParameters(parameters);
                    mConfigManager.setDesiredCameraParameters(theCamera, true);
                } catch (RuntimeException re2) { }
            }
        }
    }

    public synchronized Camera getCamera() {
        return mCamera;
    }

    public synchronized boolean isOpen() {
        return mCamera != null;
    }

    /**
     * Closes the camera driver if still in use.
     */
    public synchronized void closeDriver() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            // Make sure to clear these each time we close the camera, so that any scanning rect
            // requested by intent is forgotten.
            mFramingRect = null;
            mFramingRectInPreview = null;
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    public synchronized void startPreview() {
        Camera theCamera = mCamera;
        if (theCamera != null && !mPreviewing) {
            theCamera.startPreview();
            mPreviewing = true;
            mAutoFocusManager = new AutoFocusManager(mCamera);
            mAutoFocusManager.focus();
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    public synchronized void stopPreview() {
        if (mAutoFocusManager != null) {
            mAutoFocusManager.cancel();
            mAutoFocusManager.release();
            mAutoFocusManager = null;
        }
        if (mCamera != null && mPreviewing) {
            mCamera.stopPreview();
            //previewCallback.setHandler(null, 0);
            mPreviewing = false;
        }
    }

    /**
     * tells camera to use one time focus.
     */
    public synchronized void focus() {
        if(mAutoFocusManager != null) {
            mAutoFocusManager.focus();
        }
    }

    /**
     *
     *
     * @param newSetting if {@code true}, light should be turned on if currently off. And vice versa.
     */
    public synchronized void setTorch(boolean newSetting) {
        if (newSetting != mConfigManager.getTorchState(mCamera)) {
            if (mCamera != null) {
                if (mAutoFocusManager != null) {
                    mAutoFocusManager.cancel();
                }
                mConfigManager.setTorch(mCamera, newSetting);
                if (mAutoFocusManager != null) {
                    mAutoFocusManager.focus();
                }
            }
        }
    }

    /**
     * Allows third party apps to specify the camera ID, rather than determine
     * it automatically based on available cameras and their orientation.
     *
     * @param cameraId camera ID of the camera to use. A negative value means "no preference".
     */
    public synchronized void setManualCameraId(int cameraId) {
        mRequestedCameraId = cameraId;
    }

    /**
     * Allows third party apps to specify the scanning rectangle dimensions, rather than determine
     * them automatically based on screen resolution.
     *
     * @param width The width in pixels to scan.
     * @param height The height in pixels to scan.
     */
    public synchronized void setManualFramingRect(int width, int height) {
        if (mInitialized) {
            Point screenResolution = mConfigManager.getScreenResolution();
            if (width > screenResolution.x) {
                width = screenResolution.x;
            }
            if (height > screenResolution.y) {
                height = screenResolution.y;
            }
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
            //Log.d(TAG, "Calculated manual framing rect: " + framingRect);
            mFramingRectInPreview = null;
        } else {
            mRequestedFramingRectWidth = width;
            mRequestedFramingRectHeight = height;
        }
    }
}
