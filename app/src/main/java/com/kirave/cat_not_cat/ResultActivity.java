package com.kirave.cat_not_cat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import me.itangqi.waveloadingview.WaveLoadingView;


public class ResultActivity extends AppCompatActivity {
    WaveLoadingView mWaveLoadingView;

    private SharedPreferences sPref;
    private String path;
    private Boolean isACat;

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

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.item1) {
            Intent intent = new Intent(ResultActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar topToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        isACat = intent.getBooleanExtra("state", true);
        new AsyncTask().execute();

    }

    private class AsyncTask extends android.os.AsyncTask<Void, Void, Void>{

        private Bitmap bitmap;
        @Override
        protected Void doInBackground(Void... voids) {
            bitmap = BitmapFactory.decodeFile(String.valueOf(new File(path)));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ImageView image = (ImageView) findViewById(R.id.inn);
            giveResult();
            image.setImageBitmap(bitmap);
            super.onPostExecute(aVoid);
        }
    }

    public void onSavePicture(View view) {
        //new ClarifyConf().execute(1);
        //giveResult();
//        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);
//        mWaveLoadingView.setVisibility(View.VISIBLE);
//        mWaveLoadingView.startAnimation();
        //run();
    }

    public void onTakeNewPicture(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void giveResult(){
        if (isACat) {
            Toast.makeText(ResultActivity.this, "CAT", Toast.LENGTH_LONG).show();
            loadAndIncreaseText(MainActivity.CAT);
        } else {
            Toast.makeText(ResultActivity.this, "NOT CAT", Toast.LENGTH_LONG).show();
            loadAndIncreaseText(MainActivity.NOTCAT);
        }
        runSound(isACat);
    }

    private void runSound(boolean isACat){
        MediaPlayer mp;
        if(isACat)
        mp = MediaPlayer.create(this, R.raw.cat);
        else
            mp = MediaPlayer.create(this, R.raw.notcat);
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            } mp.start();
        } catch(Exception e) { e.printStackTrace(); }
    }


    private void loadAndIncreaseText(String cat) {
        sPref = getSharedPreferences("STATS", MODE_PRIVATE);
        int val;
        val = sPref.getInt(cat, 0);
        val++;

        saveStats(cat, val);
    }

    private void saveStats(String cat, int val) {
        sPref = getSharedPreferences("STATS", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(cat, val);
        ed.apply();
    }
}

