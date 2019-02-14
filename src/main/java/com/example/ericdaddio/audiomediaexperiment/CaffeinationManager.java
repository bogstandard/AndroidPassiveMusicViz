package com.example.ericdaddio.audiomediaexperiment;

import android.content.Context;
import android.os.PowerManager;
import android.view.WindowManager;

public class CaffeinationManager {

    CaffeinationManager(MainActivity activity) {

        // keep awake script

        // for newer phones
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // for older phones
        PowerManager powerManager = (PowerManager)activity.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                "PassiveAudioVizPrefs:CaffeinationManager");
        wakeLock.acquire();

    }
}
