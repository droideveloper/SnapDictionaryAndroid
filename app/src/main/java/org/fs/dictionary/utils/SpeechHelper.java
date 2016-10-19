package org.fs.dictionary.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fatih on 23/11/14.
 */
public class SpeechHelper {

    private MediaPlayer             mMediaPlayer    = null;
    private String                  mLocaleString   = null;
    private Context                 mContext        = null;

    private Map<String, String>     mHeader         = null;

    //private final static String URL = "http://translate.google.com/translate_tts?tl=%s&q=%s";

    private final static String STREAM_INTENT   = "com.android.music.musicservicecommand";
    private final static String STREAM_COMMAND  = "command";
    private final static String STREAM_VALUE    = "pause";

    private PrepareCallback mPrepareCallback = null;
    private BufferCallback  mBufferCallback  = null;
    private ErrorCallback   mErrorCallback   = null;

    private LogHelper       mLogHelper       = null;

    /***
     *
     * @param locale
     * @param mContext
     */
    public SpeechHelper(Locale locale, Context mContext) {
        this(locale);
        this.mContext = mContext;
    }

    /***
     *
     * @param locale
     */
    private SpeechHelper(Locale locale) {
        mLogHelper = new LogHelper(this);
        String value = locale.toString();
        if(value != null && value.contains("-")) {
            mLocaleString = localeCode(value);
        }
        initialize();
    }

    /**
     * plays the text
     * @param text
     */
    public void speak(String text) {
        if(mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            if (text != null && !TextUtils.isEmpty(text)) {
                Uri uri = toUri(trick(text), mLocaleString);
                if(mLogHelper != null) { mLogHelper.log(Log.ERROR, uri.toString()); }
                try {
                    if(mContext == null) {
                        throw new NullPointerException("Context needed to be bind on streaming api.");
                    }
                    Intent intent = new Intent(STREAM_INTENT);
                    intent.putExtra(STREAM_COMMAND, STREAM_VALUE);
                    mContext.sendBroadcast(intent);

                    mHeader = new HashMap<>();
                    mHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    mHeader.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
                    mHeader.put("Accept-Encoding", "gzip, deflate, sdch");
                    mHeader.put("Accept-Language", "tr-TR,tr;q=0.8,en-US;q=0.6,en;q=0.4");
                    mMediaPlayer.setDataSource(mContext, uri, mHeader);
                    mMediaPlayer.prepareAsync();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * stops
     */
    public void stop() {
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    public Uri toUri(String text, String language) {
        return Uri.parse("http://translate.google.com/translate_tts?tl=" + language + "&q=" + encode(text));
    }

    /***
     * release all resources we used so far.
     */
    public void release() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if(mHeader != null) {
            mHeader = null;
        }

        if(mPrepareCallback != null) {
            mPrepareCallback = null;
        }

        if(mBufferCallback != null) {
            mBufferCallback = null;
        }

        if(mErrorCallback != null) {
            mBufferCallback = null;
        }

        if(mContext != null) {
            mContext = null;
        }
        //log
        mLogHelper.log(Log.INFO, "Resources are released.");
    }

    /***
     * Header for resources to used
     * @param mHeader
     */
    public void setHeader(Map<String, String> mHeader) {
        this.mHeader = mHeader;
    }

    /***
     * Decodes text to be used in url
     * @param text text to decode.
     * @return decoded text for url use.
     */
    private String encode(String text) {
        try {
            return trick(java.net.URLEncoder.encode(text, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    String trick(String text) {
        return text.replace("Ş","S")
                   .replace("Ğ","G")
                   .replace("Ç","C")
                   .replace("İ","I")
                   .replace("Ö","O")
                   .replace("Ü","u")
                   .replace("ü", "u")
                   .replace("ş","s")
                   .replace("ç","c")
                   .replace("ı","i")
                   .replace("ö","o")
                   .replace("ğ","g");
    }

    /**
     *
     * @param language
     * @return
     */
    private String localeCode(String language) {
        String[] values = language.split("-");
        if(values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    /***
     * setter
      * @param mPrepareCallback
     */
    public void setPrepareCallback(PrepareCallback mPrepareCallback) {
        this.mPrepareCallback = mPrepareCallback;
    }

    /**
     * setter
     * @param mBufferCallback
     */
    public void setBufferCallback(BufferCallback mBufferCallback) {
        this.mBufferCallback = mBufferCallback;
    }

    /**
     * setter
     * @param mErrorCallback
     */
    public void setErrorCallback(ErrorCallback mErrorCallback) {
        this.mErrorCallback = mErrorCallback;
    }

    /**
     * initialize the media player
     */
    private void initialize() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if(mPrepareCallback != null) {
                    mPrepareCallback.onPrepare();
                }
                //auto start
                mediaPlayer.start();

                //stop();
                //release();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                if(mErrorCallback != null) {
                    mErrorCallback.onError(what, extra);
                }
                return true;
            }
        });
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int p) {
                if(mBufferCallback != null) {
                    mBufferCallback.onBuffer(p);
                }
            }
        });
    }

    /***
     * Prepare callback
     */
    public class PrepareCallback {
        public void onPrepare() { }
    }

    /***
     * Buffer callback
     */
    public class BufferCallback {
        public void onBuffer(int percentage) { }
    }

    /***
     * Error Callback
     */
    public class ErrorCallback {
        public void onError(int errorCode, int extraCode) { }
    }

    /**
     * LogHelper looks for this
     * @return
     */
    public boolean isLogEnabled() {
        return true;
    }

    /**
     * Log Helper looks for this.
     * @return
     */
    public String getClassTag() {
        return SpeechHelper.class.getSimpleName();
    }
}
