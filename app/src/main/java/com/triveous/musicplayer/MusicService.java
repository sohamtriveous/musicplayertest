package com.triveous.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by sohammondal on 22/04/15.
 */
public class MusicService extends Service {
    public static final String EXTRA_METHOD = "EXTRA_METHOD";
    public static final String EXTRA_SEEK_POSITION = "EXTRA_SEEK_POSITION";

    public static final int METHOD_PLAY = 0;
    public static final int METHOD_REWIND = 1;
    public static final int METHOD_FF = 2;
    public static final int METHOD_SEEK = 3;

    // rewind/ff seek value
    public static final int VALUE_SEEK = 1000;

    private static MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = MediaPlayer.create(this, R.raw.music);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int method = intent.getIntExtra(EXTRA_METHOD, METHOD_PLAY);
        switch (method) {
            case METHOD_PLAY:
                handlePlayEvent();
                break;
            case METHOD_REWIND:
                handleRewindEvent();
                break;
            case METHOD_FF:
                handleFFEvent();
                break;
            case METHOD_SEEK:
                int position = intent.getIntExtra(EXTRA_SEEK_POSITION, 0);
                seekTo(position);
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void handlePlayEvent() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        } else {
            mMediaPlayer.pause();
        }
    }

    private void handleRewindEvent() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - VALUE_SEEK);
    }

    private void handleFFEvent() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + VALUE_SEEK);
    }

    private void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    public static boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public static int getPosition() {
        if (mMediaPlayer != null) {
            LogUtils.logMessage("Current position: "+mMediaPlayer.getCurrentPosition());
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }
}
