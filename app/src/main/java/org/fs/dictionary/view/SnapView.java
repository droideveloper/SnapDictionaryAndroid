package org.fs.dictionary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.fs.dictionary.SnapDictionaryApplication;
import org.fs.dictionary.core.PreferenceManager;
import org.fs.dictionary.listener.RecognitionCallback;
import org.fs.dictionary.thread.RecognizeTask;
import org.fs.dictionary.utils.LogHelper;
import org.fs.dictionary.utils.SpeechHelper;
import org.fs.ghanaian.util.StringUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Fatih on 28/11/14.
 */
public class SnapView extends ImageView {

    private LogHelper   mLogHelper          = null;
    private Bitmap      mBitmap             = null;
    private Paint       mPaint              = null;
    private Paint       mBoxPaint           = null;

    private int         mLineColor          = 0;
    private int         mBoxColor           = 0;

    private Rect        mSelected           = null;

    private byte []     bytes;

    private String      text;

    private RecognizeTask mRecognizeTask     = null;
    private GestureDetector mGestureDetector = null;

    private float mX;
    private float mY;

    public SnapView(Context context, int mX, int mY) {
        super(context);
        this.mX = mX * 1.0F;
        this.mY = mY * 1.0F;

        mGestureDetector = new GestureDetector(context, new GestureCallback());
        setOnTouchListener(mTouchListener);
        initialize();
    }

    private void initialize() {
        mLineColor = Color.parseColor("#417907");
        mBoxColor  = Color.parseColor("#CC417907");
        mLogHelper = new LogHelper(this);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5.0F);
        mPaint.setColor(mLineColor);

        mBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoxPaint.setStyle(Paint.Style.FILL);
        mBoxPaint.setColor(mBoxColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mBitmap != null) {
            canvas.restore();
            canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.save();
        }

        if(mX != 0 && mY != 0) {
            float width = getWidth() * 1.0F;
            float height = getHeight() * 1.0F;

            canvas.restore();
            canvas.drawLine( mX, 0.0F, mX, height, mPaint);
            canvas.drawLine(0.0F, mY , width, mY, mPaint);
            canvas.save();
        }

        if(mSelected != null) {
            canvas.restore();
            canvas.drawRect(mSelected, mBoxPaint);
            canvas.save();

            mSelected = null;
        }
    }

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return mGestureDetector.onTouchEvent(motionEvent);
        }
    };

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;
        ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, mByteArrayOutputStream);
        bytes = mByteArrayOutputStream.toByteArray();
        try {
            mByteArrayOutputStream.flush();
            mByteArrayOutputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        invalidate();
    }

    public void setSelected(Rect mSelected) {
        this.mSelected = mSelected;
        post(new Runnable() {
            @Override
            public void run() {
                SnapView.this.invalidate();//this is running on UI thread now.
            }
        });
    }

    public void release() {
        setOnTouchListener(null);
        if(mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        if(mGestureDetector != null) {
            mGestureDetector = null;
        }

        if(mRecognizeTask != null && !mRecognizeTask.isCancelled()) {
            mRecognizeTask.cancel(true);
            mRecognizeTask = null;
        }

        if(bytes != null) {
            bytes = null;
        }
    }

    public String text() {
        return text;
    }

    public void listen() {
        if(!StringUtility.isNullOrEmpty(text)) {
            SpeechHelper mSpeechHelper = new SpeechHelper(getLocale(), SnapView.this.getContext());
            mSpeechHelper.speak(text);
        }
    }

    private Locale getLocale() {
        if("tur".equalsIgnoreCase(SnapDictionaryApplication.mPreferenceManager.getValueForKey(PreferenceManager.KEY_LANGUAGE_PREFERENCE, "tur"))) {
            return new Locale("tr-TR");
        }
        return new Locale("en-US");
    }

    public String getClassTag() {
        return SnapView.class.getSimpleName();
    }

    public boolean isLogEnabled() {
        return true;
    }

    private class GestureCallback extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) { return true; }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            mX = e.getX();
            mY = e.getY();

            invalidate();

            if(mRecognizeTask != null && !mRecognizeTask.isCancelled()) {
                mRecognizeTask.cancel(true);
            }

            mRecognizeTask = new RecognizeTask(getContext())
                    .x(Math.round(mX))
                    .y(Math.round(mY))
                    .language(SnapDictionaryApplication.mPreferenceManager.getValueForKey(PreferenceManager.KEY_LANGUAGE_PREFERENCE, "tur"))
                    .degree(0)
                    .bytes(bytes)
                    .view(SnapView.this)
                    .callback(new RecognitionCallback() {
                        @Override
                        public void onRecognitionComplete(int errorCode, String text) {
                            SnapView.this.text = text;
                            if(errorCode != -1) {
                                if (mLogHelper != null) {
                                    mLogHelper.log(Log.ERROR, text);
                                }
                            }
                        }
                    });
            mRecognizeTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, null, null, null);
            return true;
        }
    }
}
