package org.fs.dictionary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.fs.dictionary.listener.SurfaceCallback;

/**
 * Created by Fatih on 23/11/14.
 */
public final class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder             = null;
    private SurfaceCallback mSurfaceCallback  = null;

    public CameraPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CameraPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreviewView(Context context) {
        super(context);
    }

    /**
     * Call these
     */
    public void initialize() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setKeepScreenOn(true);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * call these
     * @param mSurfaceCallback
     */
    public void setSurfaceCallback(SurfaceCallback mSurfaceCallback) {
        this.mSurfaceCallback = mSurfaceCallback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(mSurfaceCallback != null) {
            mSurfaceCallback.onCreated(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if(mSurfaceCallback != null) {
            mSurfaceCallback.onChanged(surfaceHolder, format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mSurfaceCallback != null) {
            mSurfaceCallback.onDestroyed(surfaceHolder);
        }
    }
}
