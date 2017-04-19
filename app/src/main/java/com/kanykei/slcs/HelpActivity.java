package com.kanykei.slcs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import android.text.format.DateFormat;
//import java.text.SimpleDateFormat;

public class HelpActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;

    public String loadLanguage(String defaultLanguage)
    {
        SharedPreferences pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        return pref.getString("lan", defaultLanguage);
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Configuration conf = getResources().getConfiguration();
        conf.locale = myLocale;
        getResources().updateConfiguration(conf, null);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("My tag", "on create of help activity");
        String lan = loadLanguage("en");
        setLocale(lan);
        Log.i("My tag", "setLocale = " + lan);

        setContentView(R.layout.activity_help);
        Log.i("My Tag","content view activ help");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Custom action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Log.i("My Tag","action bar");

            // Create a new Fragment to be placed in the activity layout
            InfoFragment firstFragment = new InfoFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.help_frame, firstFragment).commit();

    }


    @Override
    public void onPause(){
        super.onPause();
        Log.i("My tag", "on pause of help activity");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i("My tag", "on stop of help activity");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("My tag", "on Resume of help activity");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return false;
    }

}