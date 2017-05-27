package com.kanykei.slcs;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SetTimeFragment extends Fragment implements TimePickerFragment.TimeDialogListener {
    private DBHelper mydb;
    private int position_value;
    private ArrayList<Room> array_list;
    private String message;
    private RoutinesAdapter adapter;
    private TextView empty_text;
    OutputStream outputStream;
    BluetoothSocket btSocket;
    TextView toolbar_title;
    Timer timer;
    TimerTask timerTask;

    final Handler timerHandler = new Handler(); //we are going to use a handler to be able to run in our TimerTask

    boolean restartSetTimeFragment = false;
    public SetTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String lan = loadLanguage("en");
        setLocale(lan);
        View routineView = inflater.inflate(R.layout.fragment_set_time, container, false);
        toolbar_title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        mydb = DBHelper.getInstance(getActivity());
        array_list = mydb.getAllRooms();
        if(array_list.isEmpty()){
            empty_text = (TextView) routineView.findViewById(R.id.emptyText);
            empty_text.setText(getText(R.string.empty));
        }
        message = getArguments().getString("message");
        if(message == "wake"){
            toolbar_title.setText(R.string.wake);
            adapter = new RoutinesAdapter(routineView.getContext(), array_list, "wake");
        } else if(message == "sleep") {
            toolbar_title.setText(R.string.sleep);
            adapter = new RoutinesAdapter(routineView.getContext(), array_list, "sleep");
        }else{
            toolbar_title.setText(R.string.app_name);
        }

        btSocket = ((MyBluetoothSocketApplication) getActivity().getApplication()).getBtSocket(); // get global Bluetooth Socket variable from application class
        try{
            outputStream = btSocket.getOutputStream();
            Log.i("Kani", "get  set time frag 78");
            Log.i("My tag", "beginListenForData()");
        }
        catch (IOException e){
            Toast.makeText(getActivity(),"Socket closed. Reconnect with bluetooth",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.i("My tag", "socket closed");
            btSocket = ((MyBluetoothSocketApplication) getActivity().getApplicationContext()).getBtSocket();
            try{
                outputStream = btSocket.getOutputStream();
                Log.i("Kani", "get  set time frag 88");
                outputStream.close();
                Log.i("Kani", "close  set time frag 90");
                btSocket.close();
                Log.i("My tag", "Bluetooth socket closed");
            }catch (Exception ex){
                ex.printStackTrace();
            }
            Intent intent = new Intent(getActivity(), ConnectToArduinoWithBluetooth.class);
            getActivity().startActivity(intent);
        }
        final ListView obj = (ListView) routineView.findViewById(R.id.listViewSetTime);

        obj.setAdapter(adapter);
        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> l, View arg1, int position, long id) {
                showTimePickerDialog();
                position_value = position;
            }
        });

        return routineView;
    }

    public void showTimePickerDialog() {
        TimePickerFragment dialog = new TimePickerFragment();
        dialog.setTargetFragment(this, 0);
        dialog.show(this.getFragmentManager(), "TimeDialog");
    }

    @Override
    public void onFinishDialog(String time) {
        String hours;
        String minutes;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String startTime = sdf.format(new Date());
        Log.i("My Tag", "current time is " + startTime);
        final int[] value_to_send = new int[1];
        if(message.equals("wake")) {
            mydb.updateWakeUpTimer(array_list.get(position_value).getId(), time);
            Toast.makeText(getActivity(), "Updated wake up time : " + time + " of " + array_list.get(position_value).getName(), Toast.LENGTH_SHORT).show();
            try {
                String[] startTimeArray = startTime.split(":");
                long startTimeInMilliseconds = Integer.parseInt(startTimeArray[1])*3600 + Integer.parseInt(startTimeArray[0])*216000;
                String[] endTimeArray = time.split(":");
                long endTimeInMilliseconds = Integer.parseInt(endTimeArray[1])*3600 + Integer.parseInt(endTimeArray[0])*216000;
                long diff = endTimeInMilliseconds - startTimeInMilliseconds;
                long min = (diff/3600)%60;
                long hour = (diff/3600) - min;
                hours = hour + " hours";
                minutes = min + " minutes";
                if (hour == 0) {
                    hours = "";
                }
                if (min == 0) {
                    minutes = "";
                }
                msg("Turn lights on in " + array_list.get(position_value).getName() +
                        " after " + hours + " " + minutes + ".");
                mydb.updateDelayWake(array_list.get(position_value).getId(),diff);
                ((MyBluetoothSocketApplication) getActivity().getApplication()).setTime_difference(diff);
                startTimer();


//                Handler handler_wake = new Handler();
//                handler_wake.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {//Do something after .....
//                        mydb.updateStateOfRoom(array_list.get(position_value).getId(), 1);
//                        if(array_list.get(position_value).getRelayPin() == 0){
//                            value_to_send[0] = 5;
//                        }else if(array_list.get(position_value).getRelayPin() == 1){
//                            value_to_send[0] = 6;
//                        }else if(array_list.get(position_value).getRelayPin() == 2){
//                            value_to_send[0] = 7;
//                        }else if(array_list.get(position_value).getRelayPin() == 3){
//                            value_to_send[0] = 8;
//                        }
//                        try{
//                            outputStream.write(value_to_send[0]);
//                            restartSetTimeFragment = true;
//                            Log.i("Kani", "write  set time frag 165");
//                            Log.i("My tag","set time on set time wake = " + value_to_send[0]);
//                            Toast.makeText(getActivity(), "sending set time frag"+ value_to_send[0], Toast.LENGTH_LONG).show();
//                        }catch (Exception e){
//                            Log.i("My tag", "try send in set time frag on set time wake");
//                            e.printStackTrace();
//                        }
//                        if(restartSetTimeFragment) {
//                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                            HomeFragment homeFragment = new HomeFragment();
//                            if (!homeFragment.isAdded()) {
//                                ft.replace(R.id.home_frame, homeFragment, "HomeFrag");
//                                ft.addToBackStack("HomeFrag");
//
//                                ft.commitAllowingStateLoss();
//
//                            } else {
//                                ft.remove(homeFragment);
//                                ft.add(R.id.home_frame, homeFragment);
//                                ft.addToBackStack("HomeFrag");
//
//                                ft.commitAllowingStateLoss();
//
//                            }
//                            restartSetTimeFragment = false;
//                        }
//
//
//                    }
//                }, diff);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(message.equals("sleep")){
            mydb.updateGoSleepTimer(array_list.get(position_value).getId(),time);
            Toast.makeText(getActivity(), "Updated go to sleep time : "+ time + " of " + array_list.get(position_value).getName(), Toast.LENGTH_SHORT).show();
            try {
                String[] startTimeArray = startTime.split(":");
                long startTimeInMilliseconds = Integer.parseInt(startTimeArray[1])*3600 + Integer.parseInt(startTimeArray[0])*216000;
                String[] endTimeArray = time.split(":");
                long endTimeInMilliseconds = Integer.parseInt(endTimeArray[1])*3600 + Integer.parseInt(endTimeArray[0])*216000;
                long diff = endTimeInMilliseconds - startTimeInMilliseconds;
                long min = (diff/3600)%60;
                long hour = (diff/3600) - min;
                hours = hour + " hours";
                minutes = min + " minutes";
                if (hour == 0) {
                    hours = "";
                }
                if (min == 0) {
                    minutes = "";
                }
                msg("Turn lights off in " + array_list.get(position_value).getName() +
                        " after " + hours + " " + minutes + ".");
                mydb.updateDelaySleep(array_list.get(position_value).getId(),diff);
                ((MyBluetoothSocketApplication) getActivity().getApplication()).setTime_difference(diff);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        SetTimeFragment time_fragment = new SetTimeFragment();
        time_fragment.setArguments(bundle);
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack();
        fm.beginTransaction().replace(R.id.routines_frame, time_fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(message == "wake"){
            toolbar_title.setText(R.string.wake);
        } else if(message == "sleep") {
            toolbar_title.setText(R.string.sleep);
        }else{
            toolbar_title.setText(R.string.app_name);
        }
        //onResume we start our timer so it can start when the app comes from the background
    }

    public void startTimer() {
        long firstStart = ((MyBluetoothSocketApplication) getActivity().getApplication()).getTime_difference();
        Log.i("My tag", firstStart + "difference");
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 1000*60*60*24ms = 24hours
        timer.schedule(timerTask, firstStart, 10000); //
    }

    public void stoptimertask() {
        Log.i("My tag", "stop timer task");
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                timerHandler.post(new Runnable() {
                    public void run() {
                        mydb.updateStateOfRoom(array_list.get(position_value).getId(), 1);
                        int[] value_to_send = new int[1];
                        Log.i("My tag",array_list.get(position_value).getRelayPin() + "get relay pin");
                        if(array_list.get(position_value).getRelayPin() == 0){
                            value_to_send[0] = 5;
                        }else if(array_list.get(position_value).getRelayPin() == 1){
                            value_to_send[0] = 6;
                        }else if(array_list.get(position_value).getRelayPin() == 2){
                            value_to_send[0] = 7;
                        }else if(array_list.get(position_value).getRelayPin() == 3){
                            value_to_send[0] = 8;
                        }
                        try{
                            outputStream.write(value_to_send[0]);
                            restartSetTimeFragment = true;
                            Log.i("My tag", "write  set time frag 308");
                            Log.i("My tag","set time on set time wake = " + value_to_send[0]);
                            Toast.makeText(getActivity(), "sending set time frag"+ value_to_send[0], Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            Log.i("My tag", "error: try send in set time frag on set time wake");
                            e.printStackTrace();
                        }
                        stoptimertask();

                    }
                });
            }
        };
    }

    public String loadLanguage(String defaultLanguage)
    {
        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        return pref.getString("lan", defaultLanguage);
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Configuration conf = getResources().getConfiguration();
        conf.locale = myLocale;
        getResources().updateConfiguration(conf, null);
    }

    private void msg(String s)
    {
        Log.i("My tag", s);
        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toolbar_title.setText(R.string.app_name);
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.empty_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

}


