package com.example.ericdaddio.audiomediaexperiment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

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
    protected static void setupMImageViewListeners(final MainActivity activity){

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

    protected void drawAmplitudes(MainActivity activity) {

        try {

            int vWidth = activity.mImageView.getWidth();
            int vHeight = activity.mImageView.getHeight();
            int halfHeight = vHeight / 2;

            // associate bitmap with canvas
            activity.mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
            activity.mCanvas = new Canvas(activity.mBitmap);

            // associate canvas with view
            activity.mImageView.setImageBitmap(activity.mBitmap);

            // fill canvas with background
            activity.mCanvas.drawColor(activity.mColorBackground);

            // draw circles

            // mColorAccent is an int, we can use bit magic to pluck the colour
            int alpha = 85;
            int r = (activity.mColorAccent >> 16) & 0xFF;
            int g = (activity.mColorAccent >> 8) & 0xFF;
            int b = (activity.mColorAccent >> 0) & 0xFF;
            int argb = Color.argb(alpha, r, g, b);

            // https://developer.android.com/reference/android/graphics/Xfermode
            // https://softwyer.wordpress.com/2012/01/21/1009/
            activity.mPaint.setColor(argb);
            activity.mPaint.setAntiAlias(true);
            activity.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

            double maxRadius = vWidth / activity.AMPLITUDE_VISIBLE_SIZE;
            double x = maxRadius;

            // less than full amount to fit on screen :)

            double[] visibleAmplitudePercentages = Arrays.copyOfRange(activity.amplitudePercentages,
                    0 + (activity.AMPLITUDE_VISIBLE_SIZE * activity.AMPLITUDE_VISIBLE_OFFSET),
                    activity.AMPLITUDE_VISIBLE_SIZE + (activity.AMPLITUDE_VISIBLE_SIZE * activity.AMPLITUDE_VISIBLE_OFFSET));
            for (int i = 0; i < visibleAmplitudePercentages.length - 1; i++) {

                // radius with artificial wobble for recurring amp
                // subtract rather than add so we don't add to silence!
                double radius = ((maxRadius + 100) / 100) * visibleAmplitudePercentages[i];
                radius = radius > 10 ? radius + 50 : radius ; // artificially boost it a little
                radius = (radius - Math.floor(Math.random() * 10));

                switch (activity.MODE) {

                    case "circle":
                        activity.mCanvas.drawCircle((int) x, halfHeight, (int) radius, activity.mPaint);
                        break;

                    case "bars":
                        activity.mCanvas.drawRoundRect(new RectF((int) x, (int) (halfHeight-radius), (int) (x+(maxRadius/2)), (int) (halfHeight+radius)), 6, 6, activity.mPaint);
                        break;

                }

                x += maxRadius;

            }

            // force system to redraw the view
            activity.mImageView.invalidate();
        } catch (Exception exp) {
            Log.e("Drawing", exp.getMessage());
        }

    }


}
