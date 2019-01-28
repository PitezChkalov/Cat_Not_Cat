package com.kirave.cat_not_cat;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    public void onBackPressed(){
        NavUtils.navigateUpFromSameTask(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simulateDayNight(/* DAY */ 0);
        Element adsElement = new Element();

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("This is an application that will allow you to always be right when you argue with someone about whether a subject is a cat or not.\n" +
                        "The essence of the application lies in the fact that on the screen there are two invisible buttons with opposite meaning (Two halves of the screen). When you click on one of them, the result will be a cat, when you click on another, the result will not be a cat.\n" +
                        "In order for your friends not to notice the trick, every two button presses change the values of the buttons.")
                .setImage(R.drawable.cat_wall)
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(new Element().setTitle("Cat statistics: " + loadText(MainActivity.CAT)))
                .addItem(new Element().setTitle("Not Cat statistics: " + loadText(MainActivity.NOTCAT)))
                .addGroup("Connect with us")
                .addEmail("gimbarrwow@gmail.com")
                .addInstagram("mishutkautiputi")
                .addGitHub("gimbarrwow")
                .create();

        setContentView(aboutPage);
    }

    private int loadText(String cat) {
       SharedPreferences sPref;
        sPref = getSharedPreferences("STATS", MODE_PRIVATE);
       return sPref.getInt(cat, 0);
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}