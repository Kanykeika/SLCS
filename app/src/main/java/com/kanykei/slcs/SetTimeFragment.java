package com.kanykei.slcs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class SetTimeFragment extends Fragment implements TimePickerFragment.TimeDialogListener {
    private DBHelper mydb;
    private int position_value;
    private ArrayList<Room> array_list;
    private String message;
    private RoutinesAdapter adapter;
    private TextView empty_text;

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

        mydb = DBHelper.getInstance(getContext());
        array_list = mydb.getAllRooms();
        if(array_list.isEmpty()){
            empty_text = (TextView) routineView.findViewById(R.id.emptyText);
            empty_text.setText(getText(R.string.empty));
        }
        message = getArguments().getString("message");
        if(message == "wake"){
            adapter = new RoutinesAdapter(routineView.getContext(), array_list, "wake");
        } else if(message == "sleep") {
            adapter = new RoutinesAdapter(routineView.getContext(), array_list, "sleep");
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
        if(message == "wake"){
            mydb.updateWakeUpTimer(array_list.get(position_value).getId(),time);
            Toast.makeText(getActivity(), "Updated wake up time : "+ time + " of " + array_list.get(position_value).getName(), Toast.LENGTH_SHORT).show();
        } else if(message == "sleep"){
            mydb.updateGoSleepTimer(array_list.get(position_value).getId(),time);
            Toast.makeText(getActivity(), "Updated go to sleep time : "+ time + " of " + array_list.get(position_value).getName(), Toast.LENGTH_SHORT).show();
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

}


