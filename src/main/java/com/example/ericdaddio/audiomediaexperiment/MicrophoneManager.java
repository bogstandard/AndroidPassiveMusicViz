package com.example.ericdaddio.audiomediaexperiment;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

public class MicrophoneManager {

    public static void readyMic(MediaRecorder mic) {
        mic.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mic.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mic.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD); // greater sensitivity with ACC
        //mic.setMaxDuration(5000); // this needs further testing.., does it limit the file saved in null?
        mic.setOutputFile("/dev/null"); // we don't actually want to save, but this is required :(

        try {
            mic.prepare();
        } catch (IOException ioExp) {
            Log.e("Mic Ready", "Could not prepare mic");
        }

    }

    public static Runnable processMicTicksRunnable(final MainActivity activity, final Handler handler, final int delay) {

        return new Runnable() {
                public void run() {

                    if (activity.mic == null) return;

                    try {

                        // shift array right
                        for (int i = (activity.amplitudes.length - 2); i >= 0; i--) {
                            activity.amplitudes[i + 1] = activity.amplitudes[i];
                            activity.amplitudePercentages[i + 1] = activity.amplitudePercentages[i];
                        }

                        double amplitude = activity.mic.getMaxAmplitude();

                        if (amplitude < 0 || Double.isInfinite(amplitude)) {
                            amplitude = 0.0;
                        }

                        // push latest amplitude onto front of array
                        // if it exceeds the MAX_AMPLITUDE then push that instead
                        activity.amplitudes[0] = Math.min(amplitude, activity.MAX_AMPLITUDE);

                        double percentage = (amplitude / activity.MAX_AMPLITUDE) * 100;

                        activity.amplitudePercentages[0] = Math.min(percentage, 100);

                        // apply to view
                        activity.visualsManager.drawAmplitudes(activity);

                    } catch (Exception exp) {
                        Log.e("Amplitudes Loop", exp.getMessage());
                    }

                    handler.postDelayed(this, delay);
                }
        };
    }

}
