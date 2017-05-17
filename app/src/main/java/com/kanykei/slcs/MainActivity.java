package com.kanykei.slcs;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
//import android.text.format.DateFormat;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    BluetoothSocket btSocket;
    OutputStream outputStream;
    InputStream inputStream;
    DBHelper mydb;
    private int[] tabIcons = {
            R.drawable.ic_tab_home,
            R.drawable.ic_tab_schedule,
            R.drawable.ic_tab_settings
    };
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
        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);
        mydb = DBHelper.getInstance(this);
        Log.i("My tag", "on create of main activity");
        String lan = loadLanguage("en");
        setLocale(lan);
        Log.i("My tag", "setLocale = " + lan);

//        DBHelper mydb = DBHelper.getInstance(this);
//        mydb.updateStateOfRoomEveryMinute();
//        Log.i("My tag", "mydb.updateStateOfRoomEveryMinute();");

        setContentView(R.layout.activity_main);
        Log.i("My Tag","content view activ main");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Custom action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Log.i("My Tag","action bar");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.viewpager) != null) {

            // Create a new Fragment to be placed in the activity layout
            HomeFragment firstFragment = new HomeFragment();

            Log.i("My Tag","viewpager != null");
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.viewpager, firstFragment).commit();
        }
        setupViewPager(viewPager);
        Log.i("My Tag","setupViewPager");


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        Log.i("My Tag","tabLayout");

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RootHomeFragment(), getString(R.string.title_home));
        adapter.addFragment(new RootRoutinesFragment(),  getString(R.string.title_routines));
        adapter.addFragment(new RootSettingsFragment(),  getString(R.string.title_settings));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) { return mFragmentList.get(position); }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        Log.i("My tag", "on pause of main activity");
    }

    @Override
    public void onStop(){
        super.onStop();
        btSocket = ((MyBluetoothSocketApplication) getApplication()).getBtSocket();
        try{
            outputStream = btSocket.getOutputStream();
            inputStream = btSocket.getInputStream();
            outputStream.close();
            inputStream.close();
            btSocket.close();
            Log.i("My tag", "Bluetooth socket closed");
        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent = new Intent(this, ConnectToArduinoWithBluetooth.class);
        startActivity(intent);
        Log.i("My tag", "on stop of main activity");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("My tag", "on Resume of main activity");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.moveTaskToBack(true);
                return true;
        }
        return false;
    }

}