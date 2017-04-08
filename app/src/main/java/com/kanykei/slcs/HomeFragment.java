package com.kanykei.slcs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class HomeFragment extends Fragment implements OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    private ListView obj;
    private DBHelper mydb;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private FloatingActionButton fab_voice_control;
    private TextView empty_text;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("id", 0);
        CreateRoomFragment createRoomFragment = new CreateRoomFragment();
        createRoomFragment.setArguments(dataBundle);
        getFragmentManager().beginTransaction().replace(R.id.home_frame, createRoomFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View myView = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = (Toolbar) myView.findViewById(R.id.toolbar);

        setHasOptionsMenu(true);
        mydb = DBHelper.getInstance(getContext());
        final ArrayList<Room> array_list = mydb.getAllRooms();
        if(array_list.isEmpty()){
            empty_text = (TextView) myView.findViewById(R.id.emptyText);
            empty_text.setText(getText(R.string.empty));
        }
        fab = (FloatingActionButton) myView.findViewById(R.id.fab);

        if(array_list.size() == 4){
            fab.hide();
        }else{
            fab.show();
            fab.setOnClickListener(this);
        }

        boolean voice_control = loadVoiceControl(false);
        fab_voice_control = (FloatingActionButton) myView.findViewById(R.id.fab_voice_control);

        if(!voice_control){
            fab_voice_control.hide();
        }else{
            fab_voice_control.show();
            fab_voice_control.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),"Start recognizer", Toast.LENGTH_SHORT).show();
                }
            });
        }
        final RoomAdapter adapter = new RoomAdapter(myView.getContext(),R.layout.listview, array_list);
        obj = (ListView) myView.findViewById(R.id.listView);
        obj.setAdapter(adapter);
        obj.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        obj.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", array_list.get(position).getId());
                CreateRoomFragment createRoomFragment = new CreateRoomFragment();
                createRoomFragment.setArguments(dataBundle);
                getFragmentManager().beginTransaction().replace(R.id.home_frame, createRoomFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
            }
        });
        final ViewGroup  decorView = (ViewGroup) getActivity().getWindow().getDecorView();
        obj.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = obj.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " " + getString(R.string.selected));
                // Calls toggleSelection method from ListViewAdapter Class
                adapter.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        AlertDialog.Builder aat = new AlertDialog.Builder(getContext());
                        aat.setTitle(getString(R.string.delete_dialog) + "?")
                                .setMessage(getString(R.string.delete_dialog_sure) + obj.getCheckedItemCount() + " " + getString(R.string.room) + "?")
                                .setCancelable(true)
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton(R.string.delete_dialog, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Calls getSelectedIds method from ListViewAdapter Class
                                        SparseBooleanArray selected = adapter.getSelectedIds();
                                        // Captures all selected ids with a loop
                                        for (int i = (selected.size() - 1); i >= 0; i--) {
                                            if (selected.valueAt(i)) {
                                                final Room selecteditem = adapter.getItem(selected.keyAt(i));
                                                // Remove selected items following the ids
                                                adapter.remove(selecteditem);
                                                mydb.deleteRoom(selecteditem.getId());
                                            }
                                        }

                                        Toast.makeText(getContext(), getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                                        getFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit(); //call it here to refresh listView upon delete
                                    }
                                });
                        AlertDialog art = aat.create();

                        art.show();

                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.delete_rooms, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // TODO Auto-generated method stub
                adapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

        });


        return myView;
    }

    public boolean loadVoiceControl(boolean defaultValue)
    {
        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        return pref.getBoolean("voice_control", defaultValue);
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.i("My tag", "on pause of home fragment");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.i("My tag", "on DestroyView of home fragment");
    }

    @Override
    public void onResume(){
        super.onResume();
//        String hours = "";
//        String minutes = "";
//        Log.i("My tag", "on resume of home fragment");
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//        String endTime = sdf.format(new Date());
//        Log.i("My Tag", "current time is " + endTime);
//        DBHelper mydb = DBHelper.getInstance(getContext());
//        ArrayList<Room> roomArrayList = mydb.getAllRooms();
//
//        if(roomArrayList.size() != 0) {
//            System.out.println("Turn lights on in: ");
//            for (int i = 0; i < roomArrayList.size(); i++) {
//                if(roomArrayList.get(i) != null) {
//                    String wake_time = roomArrayList.get(i).getWake();
//                    try {
//                        Date enddate = sdf.parse(endTime);
//                        Date startdate = sdf.parse(wake_time);
//                        long diff = startdate.getTime() - enddate.getTime();
//                        String differ = sdf.format(diff);
//                        String[] tokens = differ.split(":");
//                        System.out.println(roomArrayList.get(i).getName() +
//                                " after " + tokens[0] + " hours " + tokens[1] + " minutes.");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            System.out.println("Turn lights off in: ");
//            for (int i = 0; i < roomArrayList.size(); i++) {
//                if(roomArrayList.get(i) != null) {
//                    String sleep_time = roomArrayList.get(i).getSleep();
//
//                    try {
//                        Date enddate = sdf.parse(endTime);
//                        Date startdate = sdf.parse(sleep_time);
//                        long diff = startdate.getTime() - enddate.getTime();
//                        String differ = sdf.format(diff);
//                        String[] tokens = differ.split(":");
//                        hours = tokens[0] + " hours";
//                        minutes = tokens[1] + " minutes";
//                        if (tokens[0] == "00") {
//                            hours = "";
//                        }
//                        if (tokens[1] == "00") {
//                            minutes = "";
//                        }
//
//                        System.out.println(roomArrayList.get(i).getName() +
//                                " after " + hours + minutes + ".");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }



    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i("My tag", "on stop of home fragment");
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        inflater.inflate(R.menu.display_rooms, menu);
//        super.onCreateOptionsMenu(menu,inflater);
//    }

}