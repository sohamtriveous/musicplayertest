package com.triveous.musicplayer;

import android.util.Log;

/**
 * Created by sohammondal on 22/04/15.
 */
public class LogUtils {
    private static boolean shouldLog = true;

    public static void logMessage(String message) {
        if (shouldLog) {
            Log.d("MusicPlayer", message);
        }
    }
}
