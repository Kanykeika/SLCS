package com.kanykei.slcs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

public class RoutinesFragment extends Fragment{
    private TextView wake;
    private TextView sleep;
    private String lan;

    public RoutinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String lan = loadLanguage("en");
        setLocale(lan);
        final View routineView = inflater.inflate(R.layout.fragment_routines, container, false);

        wake = (TextView) routineView.findViewById(R.id.wake_up);
        sleep = (TextView) routineView.findViewById(R.id.go_to_sleep);

        wake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("message", "wake");
                SetTimeFragment wake_time_fragment = new SetTimeFragment();
                wake_time_fragment.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.routines_frame, wake_time_fragment,"routine_frag").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
            }
        });

        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("message", "sleep");
                SetTimeFragment sleep_time_fragment = new SetTimeFragment();
                sleep_time_fragment.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.routines_frame, sleep_time_fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
            }
        });

        return routineView;
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
    public void onAttach(){
        Log.i("My tag", "on attach of routine fragment");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i("My tag", "on pause of routine fragment");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.i("My tag", "on DestroyView of routine fragment");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i("My tag", "on resume of routine fragment");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i("My tag", "on stop of routine fragment");
    }
}


