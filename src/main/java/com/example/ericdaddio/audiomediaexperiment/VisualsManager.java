package com.example.ericdaddio.audiomediaexperiment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class VisualsManager {

    private static int getRGBfromXYWH(int x, int y, int w, int h) {

        int r = (int) (((((double) x / (double) w) * 100) * 255) / 100);
        int b = (int) (((((double) x / (double) h) * 100) * 255) / 100);

        int vHype = (int) Math.sqrt((w * w) + (h * h));
        int gHype = (int) Math.sqrt((x * x) + (y * y));

        int g = vHype - gHype;

        // all together now..
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;

        return -rgb;

    }

    @SuppressLint("ClickableViewAccessibility")
    public static void setupMImageViewListeners(final MainActivity activity){

        activity.mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if(!activity.INITIALIZED) {
                    activity.requestAudioPermissions();
                    return true; // return early and leave the permissions text up
                }

                int action = event.getAction() & MotionEvent.ACTION_MASK;

                // thanks https://stackoverflow.com/a/43362330
                switch (action) {

                    case MotionEvent.ACTION_DOWN: {
                        // animating layout changes means this doesn't flash when
                        // the two-finger tap is performed
                        // don't show while the options are open
                        if (activity.mOptionsView.getVisibility() != View.GONE) break;
                        activity.mHelpText.setVisibility(View.VISIBLE);
                        ++activity.mImageViewTapCount;
                        Log.d("M__", "MotionEvent.ACTION_DOWN " + activity.mImageViewTapCount);
                        break;
                    }

                    case MotionEvent.ACTION_POINTER_DOWN: {
                        ++activity.mImageViewTapCount;
                        break;
                    }

                    case MotionEvent.ACTION_POINTER_UP: {
                        ++activity.mImageViewTapCount;
                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        activity.mHelpText.setVisibility(View.GONE);
                        --activity.mImageViewTapCount;
                        if (activity.mImageViewTapCount == 2) {
                            activity.mImageViewTapCount = 0;
                            activity.mHelpText.setVisibility(View.GONE);
                            activity.mOptionsView.setVisibility(View.VISIBLE);
                            return true;
                        }
                        activity.preferenceManager.savePreferences(activity.thisInstance); // save whatever just changed
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {

                        // don't change while the options are open
                        if (activity.mOptionsView.getVisibility() != View.GONE) break;

                        activity.mColorBackground = getRGBfromXYWH(
                                (int) event.getX(),
                                (int) event.getY(),
                                view.getWidth(),
                                view.getHeight()
                        );

                        break;
                    }

                }

                return true;
            }

        });

    }

}
