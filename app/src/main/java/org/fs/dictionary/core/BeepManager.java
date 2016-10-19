package org.fs.dictionary.core;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import org.fs.dictionary.R;

import java.io.IOException;


/**
 * Created by Fatih on 27/11/14.
 */
public class BeepManager {

    private final static float BEEP_VALUME = 0.10F;
    private final static long  VIBRATION   = 200L;

    private Context     mContext;
    private MediaPlayer mMediaPlayer;
    private Vibrator    mVibrator;

    private boolean     mShouldInitialize = true;

    public BeepManager(Context mContext) {
        this.mContext = mContext;
        this.mMediaPlayer = new MediaPlayer();
        this.mVibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initialize() throws IOException {
        notifyMusicIfPlaying();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaPlayer.seekTo(0);//first position
            }
        });
        AssetFileDescriptor mAssetFileDescriptor = mContext.getResources().openRawResourceFd(R.raw.beep);
        mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
        mAssetFileDescriptor.close();
        mMediaPlayer.setVolume(BEEP_VALUME, BEEP_VALUME);
        mMediaPlayer.prepareAsync();
    }

    private void notifyMusicIfPlaying() {
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "pause");
        mContext.sendBroadcast(intent);
    }

    public void notification() {
        boolean shouldPlaySound = true; //do get it from PreferanceManager
        boolean shouldVibrate   = true; //do get it from PreferanceManager
        if(shouldPlaySound) {
            mMediaPlayer.start();
            if(shouldVibrate) {
                mVibrator.vibrate(VIBRATION);
            }
        }
    }
}
