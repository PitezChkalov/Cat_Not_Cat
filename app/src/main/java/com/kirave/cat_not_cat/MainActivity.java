package com.kirave.cat_not_cat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import me.itangqi.waveloadingview.WaveLoadingView;


public class MainActivity extends AppCompatActivity {
    WaveLoadingView mWaveLoadingView;


    Button captureCat;
    Button captureNotCat;
    TextView textViewTap;

    private static int stateCount = 0;
    private static boolean currentStateFirst = true;
    private static boolean currentStateSecond = false;
    public static final String CAT = "CAT";
    public static final String NOTCAT = "NOTCAT";

    CameraPreview preview;
    SharedPreferences sPref;
    Camera mCamera;
    FrameLayout mFrame;
    Context mContext;
    String gg = "";

    final Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Uri pictureFile = generateFile();
            try {
                savePhotoInFile(data, pictureFile);
            } catch (Exception e) {
                Toast.makeText(mContext, "Error: can't save file", Toast.LENGTH_LONG).show();
            }
            mCamera.startPreview();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item1) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        mWaveLoadingView.setProgressValue(50);
        // mWaveLoadingView.setBorderWidth(10);
        mWaveLoadingView.setAmplitudeRatio(200);
        //mWaveLoadingView.setWaveColor(Color.RED);
        //mWaveLoadingView.setBorderColor(Color.RED);
        mWaveLoadingView.setTopTitleStrokeColor(Color.GREEN);
        // mWaveLoadingView.setVisibility(View.VISIBLE);
        mWaveLoadingView.setTopTitleStrokeWidth(7);
        mWaveLoadingView.setAnimDuration(1000);
        // mWaveLoadingView.pauseAnimation();
        // mWaveLoadingView.resumeAnimation();
        // mWaveLoadingView.cancelAnimation();
        mWaveLoadingView.startAnimation();


        mContext = this;

        mCamera = openCamera();
        setParams();
        if (mCamera == null) {
            Toast.makeText(this, "Opening camera failed", Toast.LENGTH_LONG).show();
            return;
        }

        preview = new CameraPreview(this, mCamera);
        mFrame = (FrameLayout) findViewById(R.id.layout);
        mFrame.addView(preview, 0);

        captureCat = (Button) findViewById(R.id.capture_cat);
        captureNotCat = (Button) findViewById(R.id.capture_not_cat);
        textViewTap = (TextView)findViewById(R.id.textViewTap);
        captureNotCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defineState();
                takePhoto(currentStateFirst);
            }
        });
        captureCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defineState();
                takePhoto(currentStateSecond);
            }
        });

    }

    private void setParams(){
        Camera.Parameters params=mCamera.getParameters();

        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
//Camera.Size size1 = sizes.get(0);
        for(int i=0;i<sizes.size();i++)
        {

            if(sizes.get(i).width > size.width)
                size = sizes.get(i);


        }

//System.out.println(size.width + "mm" + size.height);
        params.setPictureSize(size.width, size.height);
        params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        params.setExposureCompensation(0);
        params.setPictureFormat(ImageFormat.JPEG);
        params.setJpegQuality(100);
        params.setRotation(90);


        mCamera.setParameters(params);
    }

    private void defineState(){
        if(stateCount%2==0){
            currentStateSecond = !currentStateSecond;
            currentStateFirst = !currentStateFirst;
        }
        stateCount++;
    }

    private void takePhoto(boolean state){
        textViewTap.setVisibility(View.GONE);
        captureCat.setEnabled(false);
        captureNotCat.setEnabled(false);
        new StartIntent().execute(1);
        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setVisibility(View.VISIBLE);
        mWaveLoadingView.startAnimation();
        run(state);
    }

    private Camera openCamera() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return null;

        Camera cam = null;
        if (Camera.getNumberOfCameras() > 0) {
            try {
                cam = Camera.open(0);
            } catch (Exception exc) {
                //
            }
        }
        return cam;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mWaveLoadingView.setVisibility(View.GONE);
            mFrame.removeView(preview);
            mCamera = null;
            preview = null;
            textViewTap.setVisibility(View.GONE);
        }
    }

    private Uri generateFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return null;

        File path = new File(Environment.getExternalStorageDirectory(), "CatNotCat");
        if (!path.exists()) {
            if (!path.mkdirs()) {
                return null;
            }
        }

        File newFile = new File(path.getPath() + File.separator + "1" + ".jpg");
        gg += path.getPath();
        gg += "/1.jpg";
        return Uri.fromFile(newFile);
    }


    private void savePhotoInFile(byte[] data, Uri pictureFile) throws Exception {
        if (pictureFile == null)
            throw new Exception();
        OutputStream os = getContentResolver().openOutputStream(pictureFile);
        os.write(data);
        os.close();
    }

    @SuppressLint("StaticFieldLeak")
    private class StartIntent extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... integers) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });

            mCamera.takePicture(null, null, null, mPictureCallback);
            return null;
        }
    }

    private void run(final boolean state) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra("path", gg);
                intent.putExtra("state", state);

                startActivity(intent);
            }
        }, 3000);
    }
}

