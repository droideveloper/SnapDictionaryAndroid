package org.fs.dictionary.thread;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.fs.dictionary.listener.RecognitionCallback;
import org.fs.dictionary.utils.LogHelper;
import org.fs.dictionary.view.SnapView;
import org.fs.ghanaian.util.StringUtility;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fatih on 27/11/14.
 */
public class RecognizeTask extends AsyncTask<Void, Void, String> {

    private RecognitionCallback mRecognizeCallback;

    private File mRoot;

    private byte[] mBytes;
    private int    mDegree;

    private int mX;
    private int mY;

    private SnapView mSnapView;

    private String mLang;
    private LogHelper mLogHelper;

    private Locale mLocale;

    public RecognizeTask(Context mContext) {
        mRoot = new File(mContext.getFilesDir().getAbsolutePath());
        mLogHelper = new LogHelper(this);
    }

    public RecognizeTask x(int mX) {
        this.mX = mX;
        return this;
    }

    public RecognizeTask y(int mY) {
        this.mY = mY;
        return this;
    }

    public RecognizeTask bytes(byte[] mBytes) {
        this.mBytes = mBytes;
        return this;
    }

    public RecognizeTask degree(int mDegree) {
        this.mDegree = mDegree;
        return this;
    }

    public RecognizeTask language(String mLang) {
        this.mLang = mLang;
        if("eng".equalsIgnoreCase(mLang)) {
            mLocale = new Locale("en-US");
        } else if("tur".equalsIgnoreCase(mLang)) {
            mLocale = new Locale("tr-TR");
        }
        return this;
    }

    public RecognizeTask callback(RecognitionCallback mRecognizeCallback) {
        this.mRecognizeCallback = mRecognizeCallback;
        return this;
    }

    public RecognizeTask view(SnapView mSnapView) {
        this.mSnapView  = mSnapView;
        return this;
    }

    @Override
    protected String doInBackground(Void... args) {
        if(mBytes == null) { return null; }

        Pix localPix = ReadFile.readMem(mBytes);
        if(mDegree != 0) {
            localPix = Rotate.rotate(localPix, mDegree);
        }

        localPix = Binarize.otsuAdaptiveThreshold(localPix);
        localPix = Convert.convertTo8(localPix);
        localPix = AdaptiveMap.backgroundNormMorph(localPix, 8, 6, 250);

        TessBaseAPI baseAPI = new TessBaseAPI();
        baseAPI.init(mRoot.getAbsolutePath(), mLang);
        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
        baseAPI.setImage(localPix);

        List<Rect> mBoxSet = baseAPI.getWords().getBoxRects();
        if(mBoxSet != null && mBoxSet.size() > 0) {
            int size = mBoxSet.size();
            for (int i = 0; i < size; i++) {
                Rect mBox = mBoxSet.get(i);
                if(mBox.contains(mX, mY)) {
                    baseAPI.setImage(baseAPI.getWords().getPix(i));
                    String word = baseAPI.getUTF8Text();

                    Pattern mPattern = Pattern.compile("[a-zA-Z]*");
                    Matcher mMatcher = mPattern.matcher(word);
                    if(mMatcher.find()) {
                        word = mMatcher.group()
                                      .trim()
                                      .toLowerCase(mLocale);
                    }

                    if(!StringUtility.isNullOrEmpty(word)) {
                        if(mSnapView != null) {
                            mSnapView.setSelected(mBox);
                        }
                        mLogHelper.log(Log.INFO, "*** We recognized text, pall. ***");
                    }

                    baseAPI.clear();
                    baseAPI.end();
                    localPix.recycle();

                    return word != null ? word : null;
                }
            }
        }

        baseAPI.clear();
        baseAPI.end();
        localPix.recycle();
        return null;
    }

    @Override
    protected void onPostExecute(String word) {
        super.onPostExecute(word);
        if(StringUtility.isNullOrEmpty(word)) {
            if(mRecognizeCallback != null) {
                mRecognizeCallback.onRecognitionComplete(-1, null);
            }
        } else {
            if(mRecognizeCallback != null) {
                mRecognizeCallback.onRecognitionComplete(1, word);
            }
        }
        free();
     }

    void free() {
        mLang = null;
        mRoot = null;
        mRecognizeCallback = null;
        mBytes = null;
        mLogHelper = null;
        mSnapView = null;
    }

    public boolean isLogEnabled() {
        return true;
    }

    public String getClassTag() {
        return RecognizeTask.class.getSimpleName();
    }
}
