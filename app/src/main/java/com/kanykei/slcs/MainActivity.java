package com.kanykei.slcs;
//import android.support.v13.app.FragmentPagerAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
//import android.text.format.DateFormat;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    BluetoothSocket btSocket;
    OutputStream outputStream;
    InputStream inputStream;
    static  DBHelper mydb;
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
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Log.i("My Tag","action bar");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        Log.i("My tag", "on stop of main activity");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("My tag", "on destroy of main activity");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("My tag", "on Resume of main activity");

    }

//    public void timer_handler(){
////        handler_wake.removeCallbacks(runnable);
//
//        final int[] value_to_send = new int[1];
//
//        final ArrayList<Room> roomArrayList = mydb.getAllRooms();
//
//        if (roomArrayList.size() != 0) {
////            System.out.println("Turn lights on in: ");
//            for (int i = 0; i < roomArrayList.size(); i++) {
//                if (roomArrayList.get(i) != null) {
//                    long wake_time_delay = roomArrayList.get(i).getDelay_wake();
//                    if(wake_time_delay != 0) {
//                        Handler handler_wake = new Handler();
//                        final int finalI = i;
//                        handler_wake.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {//Do something after .....
//                                mydb.updateStateOfRoom(roomArrayList.get(finalI).getId(), 1);
//                                if(roomArrayList.get(finalI).getRelayPin() == 0){
//                                    value_to_send[0] = 5;
//                                }else if(roomArrayList.get(finalI).getRelayPin() == 1){
//                                    value_to_send[0] = 6;
//                                }else if(roomArrayList.get(finalI).getRelayPin() == 2){
//                                    value_to_send[0] = 7;
//                                }else if(roomArrayList.get(finalI).getRelayPin() == 3){
//                                    value_to_send[0] = 8;
//                                }
//                                try{
//                                    outputStream.write(value_to_send[0]);
//                                    Log.i("Kani", "write  main activity 222");
//                                    Log.i("My tag","main activity on set time wake = " + value_to_send[0]);
//                                    Toast.makeText(MainActivity.this, "sending "+ value_to_send[0], Toast.LENGTH_LONG).show();
//                                }catch (Exception e){
//                                    Log.i("My tag", "try send in main activity on set time wake");
//                                    e.printStackTrace();
//                                }
//                                getFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit();
//                            }
//                        }, wake_time_delay);
//                    }
//                }
//            }
////            System.out.println("Turn lights off in: ");
//            for (int i = 0; i < roomArrayList.size(); i++) {
//                if (roomArrayList.get(i) != null) {
//                    long sleep_time_delay = roomArrayList.get(i).getDelay_sleep();
//                    if(sleep_time_delay != 0) {
//                        Handler handler_sleep = new Handler();
//                        final int finalI = i;
//                        handler_sleep.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {//Do something after .....
//                                mydb.updateStateOfRoom(roomArrayList.get(finalI).getId(), 0);
//                                if(roomArrayList.get(finalI).getRelayPin() == 0){
//                                    value_to_send[0] = 1;
//                                }else if(roomArrayList.get(finalI).getRelayPin() == 1){
//                                    value_to_send[0] = 2;
//                                }else if(roomArrayList.get(finalI).getRelayPin() == 2){
//                                    value_to_send[0] = 3;
//                                }else if(roomArrayList.get(finalI).getRelayPin() == 3){
//                                    value_to_send[0] = 4;
//                                }
//                                try{
//                                    outputStream.write(value_to_send[0]);
//                                    Log.i("Kani", "write  main activity 257");
//                                    Log.i("My tag","main activity on set time sleep = " + value_to_send[0]);
//                                    Toast.makeText(MainActivity.this, "sending "+ value_to_send[0], Toast.LENGTH_LONG).show();
//                                }catch (Exception e){
//                                    Log.i("My tag", "try send in main activity on set time sleep");
//                                    e.printStackTrace();
//                                }
//                                getFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit();
//                            }
//                        }, sleep_time_delay);
//                    }
//                }
//            }
//
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.moveTaskToBack(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}