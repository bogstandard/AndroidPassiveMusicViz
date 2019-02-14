package com.example.ericdaddio.audiomediaexperiment;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.lang.Math;
import java.util.Arrays;

import android.Manifest;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    protected boolean INITIALIZED = false;

    public String MODE = "bars";

    protected double MAX_AMPLITUDE = 32762; // from own testing & online reading, this is the max
    // but we'll cap it at this just to be safe.
    protected MediaRecorder mic;

    public int AMPLITUDE_ARRAY_SIZE = 12 * 3; // we have a hardcoded cap of 3 phones to chain
    public int AMPLITUDE_VISIBLE_SIZE = 12;
    public int AMPLITUDE_VISIBLE_OFFSET = 0; // changes dependent on phone position
    final private int TICK_FREQUENCY_MS = 50;

    final double[] amplitudes = new double[AMPLITUDE_ARRAY_SIZE];
    final double[] amplitudePercentages = new double[AMPLITUDE_ARRAY_SIZE];

    // canvas code adapted from
    // https://google-developer-training.gitbooks.io/android-developer-advanced-course-practicals/
    // content/unit-5-advanced-graphics-and-views/lesson-11-canvas/11-1a-p-create-a-simple-canvas/
    // 11-1a-p-create-a-simple-canvas.html#alreadyknow

    protected ImageView mImageView;
    private Canvas mCanvas;
    private Paint mPaint = new Paint();
    private Bitmap mBitmap;
    public int mColorBackground;
    private int mColorAccent;
    protected int mImageViewTapCount = 0;

    protected View mHelpText;
    protected View mOptionsView;

    final protected MainActivity thisInstance = this;
    private CaffeinationManager caffeinationManager;
    protected PreferenceManager preferenceManager;
    private MicrophoneManager microphoneManager;
    private VisualsManager visualsManager;

    public void drawAmplitudes(View view) {

        try {

            int vWidth = view.getWidth();
            int vHeight = view.getHeight();
            int halfHeight = vHeight / 2;

            // associate bitmap with canvas
            mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

            // associate canvas with view
            mImageView.setImageBitmap(mBitmap);

            // fill canvas with background
            mCanvas.drawColor(mColorBackground);

            // draw circles

            // mColorAccent is an int, we can use bit magic to pluck the colour
            int alpha = 85;
            int r = (mColorAccent >> 16) & 0xFF;
            int g = (mColorAccent >> 8) & 0xFF;
            int b = (mColorAccent >> 0) & 0xFF;
            int argb = Color.argb(alpha, r, g, b);

            // https://developer.android.com/reference/android/graphics/Xfermode
            // https://softwyer.wordpress.com/2012/01/21/1009/
            mPaint.setColor(argb);
            mPaint.setAntiAlias(true);
            mPaint.setXfermode(new PorterDuffXfermode(Mode.ADD));

            double maxRadius = vWidth / AMPLITUDE_VISIBLE_SIZE;
            double x = maxRadius;

            // less than full amount to fit on screen :)

            double[] visibleAmplitudePercentages = Arrays.copyOfRange(amplitudePercentages,
                    0 + (AMPLITUDE_VISIBLE_SIZE * AMPLITUDE_VISIBLE_OFFSET),
                    AMPLITUDE_VISIBLE_SIZE + (AMPLITUDE_VISIBLE_SIZE * AMPLITUDE_VISIBLE_OFFSET));

            for (int i = 0; i < visibleAmplitudePercentages.length - 1; i++) {

                // radius with artificial wobble for recurring amp
                // subtract rather than add so we don't add to silence!
                double radius = (((maxRadius + 100) / 100) * visibleAmplitudePercentages[i]) - Math.floor(Math.random() * 10);

                switch (MODE) {

                    case "circle":
                        mCanvas.drawCircle((int) x, halfHeight, (int) radius, mPaint);
                        break;

                    case "bars":
                        mCanvas.drawRoundRect(new RectF((int) x, (int) (halfHeight-radius), (int) (x+(maxRadius/2)), (int) (halfHeight+radius)), 6, 6, mPaint);
                        break;

                }

                x += maxRadius;

            }

            // force system to redraw the view
            view.invalidate();
        } catch (Exception exp) {
            Log.e("Drawing", exp.getMessage());
        }

    }

    public void optionsButtonOnClick(View view) {
        int multiplier;
        switch (view.getId()) {
            case R.id.buttonChainPos0:
                AMPLITUDE_VISIBLE_OFFSET = 0;
                break;

            case R.id.buttonChainPos1:
                AMPLITUDE_VISIBLE_OFFSET = 1;
                break;

            case R.id.buttonChainPos2:
                AMPLITUDE_VISIBLE_OFFSET = 2;
                break;
        }

        Log.i("Options", "Button clicked.");
        mOptionsView.setVisibility(View.GONE);
    }

    public void styleSwitcherButtonOnClick(View view) {

        switch (MODE) {
            case "circle":
                view.setBackgroundResource(R.drawable.button_style_switcher_bars);
                MODE = "bars";
                break;

            case "bars":
                view.setBackgroundResource(R.drawable.button_style_switcher_circle);
                MODE = "circle";
                break;
        }

        preferenceManager.savePreferences(thisInstance);
        Log.i("Options", "Button clicked.");
    }

    public void optionsBackgroundOnClick(View view) {
        mOptionsView.setVisibility(View.GONE);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // doing it via the layout on older phones causes confusion becuase its a sub-layout
        // the method cannot be found normally because it looks elsewhere!
        //findViewById(R.id.myOptionsLayout).setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        optionsButtonOnClick(v);
        //    }
        //});

        caffeinationManager = new CaffeinationManager(thisInstance);
        preferenceManager = new PreferenceManager(thisInstance);
        microphoneManager = new MicrophoneManager();
        visualsManager = new VisualsManager();

        setContentView(R.layout.activity_main);

        mOptionsView = findViewById(R.id.myOptionsLayout);
        mHelpText = findViewById(R.id.myHelpText);


        /**
         * Begin canvas bootstrapping code
         */

        mImageView = findViewById(R.id.myimageview);

        mColorBackground = ResourcesCompat.getColor(getResources(),
                R.color.colorBackground, null);
        mColorAccent = ResourcesCompat.getColor(getResources(),
                R.color.colorAccent, null);

        preferenceManager.restorePreferences(thisInstance);

        switch (MODE) {
            case "circle":
                findViewById(R.id.styleSwitcher).setBackgroundResource(R.drawable.button_style_switcher_circle);
                break;

            case "bars":
                findViewById(R.id.styleSwitcher).setBackgroundResource(R.drawable.button_style_switcher_bars);
                break;
        }

        visualsManager.setupMImageViewListeners(thisInstance);

        requestAudioPermissions(); // in turn calls initialize() dependent on result

    }

    private void initialize() {

        INITIALIZED = true;

        ((TextView) findViewById(R.id.myHelpText)).setText(getResources().getString(R.string.help));

        /**
         * Begin amplitude loop code
         */
        final Handler handler = new Handler();
        final int delay = TICK_FREQUENCY_MS; //milliseconds
        mic = new MediaRecorder();
        microphoneManager.readyMic(mic);
        mic.start();   // Initial record start before we get into a loop
        handler.postDelayed(microphoneManager.processMicTicksRunnable(thisInstance, handler, delay), delay);
    }


    // adapted from https://github.com/ptyagicodecamp/android-recipes/blob/develop/AudioRuntimePermissions/app/src/main/java/org/pcc/audioruntimepermissions/MainActivity.java

    //Requesting run-time permissions

    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    protected void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            initialize();
        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    initialize();
                }
                return;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mic != null) {
            mic.reset();
            mic.release();
            mic = null;
        }
    }

}
