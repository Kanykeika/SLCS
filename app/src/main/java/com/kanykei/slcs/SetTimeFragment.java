package com.kanykei.slcs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import java.util.ArrayList;

//import android.app.DialogFragment;

public class SetTimeFragment extends Fragment implements TimePickerFragment.TimeDialogListener {
    private DBHelper mydb;
    private String time_value;

    public SetTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View routineView = inflater.inflate(R.layout.fragment_set_time, container, false);

        mydb = new DBHelper(getActivity());
        final ArrayList<Room> array_list = mydb.getAllRooms();

        RoutinesAdapter adapter = new RoutinesAdapter(routineView.getContext(), array_list);
        final ListView obj = (ListView) routineView.findViewById(R.id.listViewSetTime);

        obj.setAdapter(adapter);
        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> l, View arg1, int position, long id) {
                showTimePickerDialog();
                String message = getArguments().getString("message");
                if(message == "wake"){
                    mydb.updateWakeUpTimer(array_list.get(position).getId(),time_value);
                } else if(message == "sleep") {
                    mydb.updateGoSleepTimer(array_list.get(position).getId(),time_value);
                }
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
        Toast.makeText(getActivity(), "Selected Time : "+ time, Toast.LENGTH_SHORT).show();
        time_value = time;
    }

}


