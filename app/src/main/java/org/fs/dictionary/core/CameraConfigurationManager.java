package org.fs.dictionary.core;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Display;
import android.view.WindowManager;
import org.fs.dictionary.utils.CameraConfigurationUtils;

import java.util.List;

/**
 * Created by Fatih on 23/11/14.
 */
final class CameraConfigurationManager {

    private final Context       mContext;
    private Point               mScreenResolution;
    private Point               mCameraResolution;

    private PreferenceManager   mPreferenceManager;

    /**
     * Constructor
     * @param mContext
     * @param mPreferenceManager
     */
    public CameraConfigurationManager(Context mContext, PreferenceManager mPreferenceManager) {
        this.mContext = mContext;
        this.mPreferenceManager = mPreferenceManager;
    }

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    void initFromCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);
        mScreenResolution = theScreenResolution;
        mCameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, mScreenResolution);
    }

    /**
     * Camera parameters optimization
     * @param camera
     * @param safeMode
     */
    void setDesiredCameraParameters(Camera camera, boolean safeMode) {
        Camera.Parameters parameters = camera.getParameters();
        if (parameters == null) { return; }
        initializeTorch(parameters, mPreferenceManager, safeMode);
        if (!safeMode) {
            if (true) {//!mPreferenceManager.getValueForKey(PreferenceManager.KEY_DISABLE_METERING, true)) {
                CameraConfigurationUtils.setVideoStabilization(parameters);
                CameraConfigurationUtils.setFocusArea(parameters);
                CameraConfigurationUtils.setMetering(parameters);
            }
        }
        parameters.setPreviewSize(mCameraResolution.x, mCameraResolution.y);
        camera.setParameters(parameters);
        Camera.Parameters afterParameters = camera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null && (mCameraResolution.x != afterSize.width || mCameraResolution.y != afterSize.height)) {
            mCameraResolution.x = afterSize.width;
            mCameraResolution.y = afterSize.height;
        }
    }

    /**
     * camera resolution
     * @return
     */
    Point getCameraResolution() {
        return mCameraResolution;
    }

    /**
     * screen resolution
     * @return
     */
    Point getScreenResolution() {
        return mScreenResolution;
    }

    /**
     * torch state if possible
     * @param camera
     * @return
     */
    boolean getTorchState(Camera camera) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters != null) {
                String flashMode = parameters.getFlashMode();
                return flashMode != null &&
                        (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) ||
                                Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode));
            }
        }
        return false;
    }

    /**
     * torch setter
     * @param camera
     * @param newSetting
     */
    void setTorch(Camera camera, boolean newSetting) {
        Camera.Parameters parameters = camera.getParameters();
        doSetTorch(parameters, newSetting, false);
        camera.setParameters(parameters);
    }

    /**
     * initializes the best photo preview and picture size same for better use.
     * @param camera
     */
    void setPhotoResolution(Camera camera) {
        if(camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        if(parameters == null) {
            return;
        }
        Camera.Size mPreviewSize = parameters.getPreviewSize();
        List<Camera.Size> photoSizes = parameters.getSupportedPictureSizes();
        int size = photoSizes.size();
        for(int i = 0; i < size; i++) {
            Camera.Size mSize = photoSizes.get(i);
            if(mSize != null && mPreviewSize != null) {
                if((mSize.width == mPreviewSize.width) && (mSize.height == mPreviewSize.height)) {
                    parameters.setPictureSize(mSize.width, mSize.height);
                    camera.setParameters(parameters);
                    break;
                }
            }
        }
    }

    /**
     * torch initializer
     * @param parameters
     * @param mPreferenceManager
     * @param safeMode
     */
    private void initializeTorch(Camera.Parameters parameters, PreferenceManager mPreferenceManager, boolean safeMode) {
        boolean currentSetting = true;//FrontLightMode.parse(mPreferenceManager) == FrontLightMode.ON;
        doSetTorch(parameters, currentSetting, safeMode);
    }

    /**
     * final step of torch implementation
     * @param parameters
     * @param newSetting
     * @param safeMode
     */
    private void doSetTorch(Camera.Parameters parameters, boolean newSetting, boolean safeMode) {
        CameraConfigurationUtils.setTorch(parameters, newSetting);
        boolean exposure = true;//mPreferenceManager.getValueForKey(PreferenceManager.KEY_DISABLE_EXPOSURE, true);
        if (!safeMode && !exposure) {
            CameraConfigurationUtils.setBestExposure(parameters, newSetting);
        }
    }
}