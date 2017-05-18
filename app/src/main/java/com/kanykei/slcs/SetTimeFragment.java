package com.kanykei.slcs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SetTimeFragment extends Fragment implements TimePickerFragment.TimeDialogListener {
    private DBHelper mydb;
    private int position_value;
    private ArrayList<Room> array_list;
    private String message;
    private RoutinesAdapter adapter;
    private TextView empty_text;
    TextView toolbar_title;
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
        mydb = DBHelper.getInstance(getContext());
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
                ((MainActivity)getActivity()).timer_handler();

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
                ((MainActivity)getActivity()).timer_handler();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        SetTimeFragment time_fragment = new SetTimeFragment();
        time_fragment.setArguments(bundle);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
        fm.beginTransaction().replace(R.id.routines_frame, time_fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
    }
    public String loadLanguage(String defaultLanguage)
    {
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
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


