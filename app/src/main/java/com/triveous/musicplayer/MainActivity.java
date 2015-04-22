package com.triveous.musicplayer;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    // refresh interval in milliseconds
    public static final int INTERVAL_REFRESH = 500;

    private ImageView mPlayButton;
    private View mRewindButton;
    private View mFFButton;
    private MusicHandler mMusicHandler = new MusicHandler(Looper.getMainLooper());

    private SeekBar mSeekbar;

    private void startPlayback() {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(MusicService.EXTRA_METHOD, MusicService.METHOD_PLAY);
        startService(intent);
    }

    private void fastForward() {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(MusicService.EXTRA_METHOD, MusicService.METHOD_FF);
        startService(intent);
    }

    private void rewind() {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(MusicService.EXTRA_METHOD, MusicService.METHOD_REWIND);
        startService(intent);
    }

    private void seekTo(int position) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(MusicService.EXTRA_METHOD, MusicService.METHOD_SEEK);
        intent.putExtra(MusicService.EXTRA_SEEK_POSITION, position);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mPlayButton = (ImageView) findViewById(R.id.activity_music_play);

        mRewindButton = findViewById(R.id.activity_music_rewind);
        mFFButton = findViewById(R.id.activity_music_ff);

        mSeekbar = (SeekBar) findViewById(R.id.activity_music_seekbar);

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
                startPlayback();
                mMusicHandler.sendEmptyMessage(MusicHandler.MESSAGE_UPDATE_SEEKBAR);
                Toast.makeText(MainActivity.this, getString(R.string.activity_main_playing), Toast.LENGTH_SHORT).show();
            }
        });

        mRewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewind();
            }
        });

        mFFButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fastForward();
            }
        });
    }

    /**
     * Update handler, seekbar once the media has finished playback
     */
    private void setOnCompletionListener() {
    }

    /**
     * Update the mediaplayer position once the seekbar is clicked
     */
    private void setOnSeekBarChangeListener() {
        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekTo(progress);
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
                if (MusicService.isPlaying()) {
                    // update seekbar
                    mSeekbar.setProgress(MusicService.getPosition());
                    // schedule the next message
                    sendEmptyMessageDelayed(MESSAGE_UPDATE_SEEKBAR, INTERVAL_REFRESH);
                }
            }
            super.handleMessage(msg);
        }
    }
}
