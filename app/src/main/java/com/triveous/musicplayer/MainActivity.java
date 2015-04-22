package com.triveous.musicplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    // refresh interval in milliseconds
    public static final int INTERVAL_REFRESH = 500;
    // rewind/ff seek value
    public static final int VALUE_SEEK = 1000;

    private boolean shouldLog = true;

    private Button mPlayButton;
    private Button mPauseButton;
    private TextView mStatusTextView;
    private View mRewindButton;
    private View mFFButton;
    private MusicHandler mMusicHandler = new MusicHandler(Looper.getMainLooper());

    private MediaPlayer mMediaPlayer;

    private SeekBar mSeekbar;

    private void logMessage(String message) {
        if (shouldLog) {
            Log.d("MainActivity", message);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaPlayer = MediaPlayer.create(this, R.raw.music);

        mPlayButton = (Button) findViewById(R.id.activity_main_play);
        mPauseButton = (Button) findViewById(R.id.activity_main_pause);
        mStatusTextView = (TextView) findViewById(R.id.activity_main_status);

        mRewindButton = findViewById(R.id.activity_main_rewind);
        mFFButton = findViewById(R.id.activity_main_ff);

        mSeekbar = (SeekBar) findViewById(R.id.activity_main_seekbar);

        setOnClickListeners();
        setOnCompletionListener();
        setOnSeekBarChangeListener();
    }

    /**
     * Add onClickListeners to all views
     */
    private void setOnClickListeners() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getString(R.string.activity_main_playing), Toast.LENGTH_SHORT).show();
                // start playing music
                mMediaPlayer.start();
                // initialize the seekbar
                mSeekbar.setMax(mMediaPlayer.getDuration());
                // start updating the seekbar
                mMusicHandler.sendEmptyMessage(MusicHandler.MESSAGE_UPDATE_SEEKBAR);
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, getString(R.string.activity_main_pause), Toast.LENGTH_SHORT).show();
                mMediaPlayer.pause();
                mMusicHandler.removeMessages(MusicHandler.MESSAGE_UPDATE_SEEKBAR);
            }
        });

        mRewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - VALUE_SEEK);
            }
        });

        mFFButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - VALUE_SEEK);
            }
        });
    }

    /**
     * Update handler, seekbar once the media has finished playback
     */
    private void setOnCompletionListener() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, getString(R.string.activity_main_finished), Toast.LENGTH_SHORT)
                        .show();
                mSeekbar.setProgress(0);
                mMusicHandler.removeMessages(MusicHandler.MESSAGE_UPDATE_SEEKBAR);
            }
        });
    }

    /**
     * Update the mediaplayer position once the seekbar is clicked
     */
    private void setOnSeekBarChangeListener() {
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * A handler to update the seekbar
     */
    private class MusicHandler extends Handler {
        // message to update the seekbar
        public static final int MESSAGE_UPDATE_SEEKBAR = 1;

        private MusicHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_UPDATE_SEEKBAR) {
                if (mMediaPlayer.isPlaying()) {
                    // update seekbar
                    mSeekbar.setProgress(mMediaPlayer.getCurrentPosition());
                    // schedule the next message
                    sendEmptyMessageDelayed(MESSAGE_UPDATE_SEEKBAR, INTERVAL_REFRESH);
                }
            }
            super.handleMessage(msg);
        }
    }
}
