package org.fs.dictionary.core;

import android.hardware.Camera;

/**
 * Created by Fatih on 23/11/14.
 */
public final class OpenCamera {

    /**
     * Open camera with given id of parameters
     * @param id
     * @return
     */
    public static Camera open(int id)  {
        int cameraCount = Camera.getNumberOfCameras();
        if(cameraCount == 0) {
            return null;
        }

        boolean req = id >= 0;

        if(!req) {
            int i = 0;
            while (i < cameraCount) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break;
                }
            }

            id = i;
        }

        Camera camera;
        if(id < cameraCount) {
            camera = Camera.open(id);
        } else {
            if(req) {
                camera = null;
            } else {
                camera = Camera.open(0);
            }
        }
        return camera;
    }

    /**
     * Camera open with 0 or back camera only.
     * @return
     */
    public static Camera open() {
        return open(-1);
    }
}
