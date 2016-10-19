package org.fs.dictionary.core;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Fatih on 23/11/14.
 */
public final class AmbientLightManager implements SensorEventListener {

    private static final float TOO_DARK_LUX         = 45.0f;
    private static final float BRIGHT_ENOUGH_LUX    = 450.0f;

    private final   Context         mContext;
    private         CameraManager   mCameraManager;
    private         Sensor          mLightSensor;

    public AmbientLightManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * start function needs CameraManager Instance Object
     * @param mCameraManager
     */
    public void start(CameraManager mCameraManager) {
        this.mCameraManager = mCameraManager;
        SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLightSensor != null) {
            sensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /***
     * start and stop methods
     */
    public void stop() {
        if (mLightSensor != null) {
            SensorManager sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
            mCameraManager = null;
            mLightSensor = null;
        }
    }

    /**
     * To get environment is dark or not
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float ambientLightLux = sensorEvent.values[0];
        if (mCameraManager != null) {
            if (ambientLightLux <= TOO_DARK_LUX) {
                mCameraManager.setTorch(true);
            } else if (ambientLightLux >= BRIGHT_ENOUGH_LUX) {
                mCameraManager.setTorch(false);
            }
        }
    }

    /**
     * Accuracy of the sensor event
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
