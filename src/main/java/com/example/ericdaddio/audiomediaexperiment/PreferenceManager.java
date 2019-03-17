package com.example.ericdaddio.audiomediaexperiment;

import android.content.SharedPreferences;

public class PreferenceManager {

    public static final String PREFS_NAME = "PassiveAudioVizPrefs";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    PreferenceManager(MainActivity activity) {
        sharedPreferences = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, activity.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    public void savePreferences(MainActivity activity) {
        sharedPreferencesEditor.putInt("AMPLITUDE_VISIBLE_OFFSET", activity.AMPLITUDE_VISIBLE_OFFSET);
        sharedPreferencesEditor.putInt("mColorBackground", activity.mColorBackground);
        sharedPreferencesEditor.putString("MODE", activity.MODE);
        sharedPreferencesEditor.commit();
    }

    public void restorePreferences(MainActivity activity) {
        activity.AMPLITUDE_VISIBLE_OFFSET = sharedPreferences.getInt("AMPLITUDE_VISIBLE_OFFSET", activity.AMPLITUDE_VISIBLE_OFFSET);
        activity.mColorBackground = sharedPreferences.getInt("mColorBackground", activity.mColorBackground);
        activity.MODE = sharedPreferences.getString("MODE", activity.MODE);
    }

}
