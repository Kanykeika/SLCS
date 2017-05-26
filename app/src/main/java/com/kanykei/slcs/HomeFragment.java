package com.kanykei.slcs;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Integer.parseInt;


public class HomeFragment extends Fragment implements TextToSpeech.OnInitListener, OnClickListener {

    private ListView group_list_view;

    public HomeFragment() {
        // Required empty public constructor
    }

    Activity mContext;
    TextView toolbar_title;
    // recognizer
    TextToSpeech tts;
    boolean restartFlag = false;
    Spinner spinnerResult;
    OutputStream outputStream;
    InputStream inputStream;
    Thread workerThread;
    volatile boolean stopWorker;
    BluetoothSocket btSocket;
    private static final int RQS_RECOGNITION = 1;
    // CRUD rooms
    private ListView obj;
    private DBHelper mydb;
    private FloatingActionButton fab;
    private Button fab_voice_control;
    private TextView empty_text;
    ArrayList<Room> array_list;
    ArrayList<Group> group_array_list;
    int value_to_send;
    View myView;
    int counter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("My tag", "on create home fragment");
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
        Log.i("My tag", "on create view home fragment");

        myView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        toolbar_title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.app_name);
        mydb = DBHelper.getInstance(getContext());
        array_list = mydb.getAllRooms();
        group_array_list = mydb.getAllGroups();
        if(array_list.isEmpty() && group_array_list.isEmpty()){
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

        //-----------------------
        //speech recognition code

        boolean voice_control = loadVoiceControl(false);


        fab_voice_control = (Button) myView.findViewById(R.id.fab_voice_control);

        if(!voice_control){
            fab_voice_control.setVisibility(View.INVISIBLE);
        }else{
            fab_voice_control.setVisibility(View.VISIBLE);
            fab_voice_control.setOnClickListener(startRecognizerOnClickListener);
            spinnerResult = (Spinner) myView.findViewById(R.id.result);
            tts = new TextToSpeech(getContext(), this);
        }
         btSocket = ((MyBluetoothSocketApplication) getActivity().getApplication()).getBtSocket(); // get global Bluetooth Socket variable from application class

         try{
             outputStream = btSocket.getOutputStream();
             Log.i("Kani", "get  home frag 158");
             inputStream = btSocket.getInputStream();
             counter = ((MyBluetoothSocketApplication) getActivity().getApplication()).getCounter();
             if(counter != 1){
                 outputStream.write(9);
                 ((MyBluetoothSocketApplication) getActivity().getApplication()).setCounter(1);
                 Log.i("My tag", "sending 9");
             }beginListenForData();
             Log.i("My tag", "beginListenForData()");
        }
        catch (IOException e){
            Toast.makeText(getActivity(),"Socket closed. Reconnect with bluetooth",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            Log.i("My tag", "socket closed");
            btSocket = ((MyBluetoothSocketApplication) getActivity().getApplicationContext()).getBtSocket();
            try{
                outputStream = btSocket.getOutputStream();
                Log.i("Kani", "get  home frag 160");
                inputStream = btSocket.getInputStream();
                outputStream.close();
                Log.i("Kani", "close  home frag 160");
                inputStream.close();
                btSocket.close();
                Log.i("My tag", "Bluetooth socket closed");
            }catch (Exception ex){
                ex.printStackTrace();
            }
            Intent intent = new Intent(getActivity(), ConnectToArduinoWithBluetooth.class);
            getActivity().startActivity(intent);
        }




        ///////////////////////////////////////////////////////////////////////////////////////////////////
        ///////         ROOMS        //////////////////////////////////////////////////////////////////////
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
                    case R.id.group:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected_group_to_create = adapter.getSelectedIds();
                        ArrayList<Integer> room_ids = new ArrayList<Integer>();
                        Bundle dataBundle = new Bundle();
                        // Captures all selected ids with a loop
                        for (int i = 0; i < selected_group_to_create.size(); i++) {
                            if (selected_group_to_create.valueAt(i)) {
                                final Room selecteditem = adapter.getItem(selected_group_to_create.keyAt(i));
                                room_ids.add(selecteditem.getId());
                            }
                        }
                        dataBundle.putIntegerArrayList("room_ids", room_ids);
                        CreateGroupFragment createGroupFragment = new CreateGroupFragment();
                        createGroupFragment.setArguments(dataBundle);
                        getFragmentManager().beginTransaction().replace(R.id.home_frame, createGroupFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).commit();
                        // Close CAB
                        mode.finish();
                        return true;
                    case R.id.delete:
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
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit(); //call it here to refresh listView upon delete
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.delete_groups, menu);
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


///////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////           GROUPS                 /////////////////////////////////////////////////////

        final GroupAdapter groupAdapter = new GroupAdapter(myView.getContext(),R.layout.group_listview, group_array_list);
        group_list_view = (ListView) myView.findViewById(R.id.group_listView);
        group_list_view.setAdapter(groupAdapter);
        group_list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        group_list_view.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position_group, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = group_list_view.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " " + getString(R.string.selected));
                // Calls toggleSelection method from ListViewAdapter Class
                groupAdapter.toggleSelection(position_group);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected_group = groupAdapter.getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected_group.size() - 1); i >= 0; i--) {
                            if (selected_group.valueAt(i)) {
                                final Group selecteditem_group = groupAdapter.getItem(selected_group.keyAt(i));
                                // Remove selected items following the ids
                                groupAdapter.remove(selecteditem_group);
                                mydb.deleteGroup(selecteditem_group.getId());
                            }
                        }
                        Toast.makeText(getContext(), getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit(); //call it here to refresh listView upon delete
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
                groupAdapter.removeSelection();

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

        });


        return myView;
    }

    public void beginListenForData() {

        stopWorker = false;
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                byte[] buffer = new byte[1024];
                int bytes;

                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        bytes = inputStream.read(buffer);
                        String strReceived = new String(buffer, 0, bytes);
                        msg(String.valueOf(bytes) + " bytes received:\n" + strReceived);
                        switch (strReceived) {
                            case "1":
                                mydb.updateStateOfRoomByRelayPin(0, 0);
                                Log.i("My tag", "case 1");
                                restartFlag = true;
                                break;
                            case "2":
                                mydb.updateStateOfRoomByRelayPin(1, 0);
                                Log.i("My tag", "case 2");
                                restartFlag = true;
                                break;
                            case "3":
                                mydb.updateStateOfRoomByRelayPin(2, 0);
                                Log.i("My tag", "case 3");
                                restartFlag = true;
                                break;
                            case "4":
                                mydb.updateStateOfRoomByRelayPin(3, 0);
                                Log.i("My tag", "case 4");
                                restartFlag = true;
                                break;
                            case "5":
                                mydb.updateStateOfRoomByRelayPin(0, 1);
                                Log.i("My tag", "case 5");
                                restartFlag = true;
                                break;
                            case "6":
                                mydb.updateStateOfRoomByRelayPin(1, 1);
                                Log.i("My tag", "case 6");
                                restartFlag = true;
                                break;
                            case "7":
                                mydb.updateStateOfRoomByRelayPin(2, 1);
                                Log.i("My tag", "case 7");
                                restartFlag = true;
                                break;
                            case "8":
                                mydb.updateStateOfRoomByRelayPin(3, 1);
                                Log.i("My tag", "case 8");
                                restartFlag = true;
                                break;
                        }
                        if(restartFlag) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            HomeFragment homeFragment = new HomeFragment();
                            if (!homeFragment.isAdded()) {
                                ft.replace(R.id.home_frame, homeFragment);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();

                            } else {
                                ft.remove(homeFragment);
                                ft.add(R.id.home_frame, homeFragment);
                                ft.addToBackStack(null);
                                ft.commitAllowingStateLoss();

                            }
                            restartFlag = false;
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        msg("Connection lost:\n"
                                + e.getMessage());
                        Toast.makeText(getContext(), "Connection lost: "+ e.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), ConnectToArduinoWithBluetooth.class);
                        startActivity(intent);

                    }
                }
                if(Thread.currentThread().isInterrupted()){
                    Intent intent = new Intent(getActivity(), ConnectToArduinoWithBluetooth.class);
                    startActivity(intent);
                }
            }
        });

        workerThread.start();
    }



    private void msg(String s)
    {
        Log.i("My tag", s);
    }

    //----------------------------
    //speech recognition functions
    private Button.OnClickListener startRecognizerOnClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to Recognize");
            startActivityForResult(intent, RQS_RECOGNITION);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == RQS_RECOGNITION) & (resultCode == Activity.RESULT_OK)) {
//        if (requestCode == RQS_RECOGNITION) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerResult.setAdapter(adapter);
            spinnerResult.setOnItemSelectedListener(spinnerResultOnItemSelectedListener);

        }
    }

    private Spinner.OnItemSelectedListener spinnerResultOnItemSelectedListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedResult = parent.getItemAtPosition(position).toString();
            Log.i("My tag","value_to_send 0 = " + value_to_send);
            for(int i = 0; i < array_list.size(); i++){
                if(selectedResult.toLowerCase().equals("turn on " + array_list.get(i).getName()) ||
                        selectedResult.toLowerCase().equals("включить " + array_list.get(i).getName()) ||
                        selectedResult.toLowerCase().equals(array_list.get(i).getName() + "turn on ") ||
                        selectedResult.toLowerCase().equals(array_list.get(i).getName() + "включить ")){
                    if(array_list.get(i).getRelayPin() == 0){
                        value_to_send = 5;
                    }else if(array_list.get(i).getRelayPin() == 1){
                        value_to_send = 6;
                    }else if(array_list.get(i).getRelayPin() == 2){
                        value_to_send = 7;
                    }else if(array_list.get(i).getRelayPin() == 3){
                        value_to_send = 8;
                    }
                }else
                if(selectedResult.toLowerCase().equals("turn off " + array_list.get(i).getName()) ||
                        selectedResult.toLowerCase().equals("выключить " + array_list.get(i).getName()) ||
                        selectedResult.toLowerCase().equals("отключить " + array_list.get(i).getName()) ||
                        selectedResult.toLowerCase().equals(array_list.get(i).getName() + "turn off ") ||
                        selectedResult.toLowerCase().equals(array_list.get(i).getName() + "выключить ") ||
                        selectedResult.toLowerCase().equals(array_list.get(i).getName() + "отключить ")){
                    if(array_list.get(i).getRelayPin() == 0){
                        value_to_send = 1;
                    }else if(array_list.get(i).getRelayPin() == 1){
                        value_to_send = 2;
                    }else if(array_list.get(i).getRelayPin() == 2){
                        value_to_send = 3;
                    }else if(array_list.get(i).getRelayPin() == 3){
                        value_to_send = 4;
                    }
                }
            }
            switch (value_to_send) {
                case 1:
                    mydb.updateStateOfRoomByRelayPin(0, 0);
                    Log.i("My tag","mydb.updateStateOfRoomByRelayPin(0, 0);");

                    break;
                case 2:
                    mydb.updateStateOfRoomByRelayPin(1, 0);
                    Log.i("My tag","mydb.updateStateOfRoomByRelayPin(1, 0);");

                    break;
                case 3:
                    mydb.updateStateOfRoomByRelayPin(2, 0);
                    Log.i("My tag","mydb.updateStateOfRoomByRelayPin(2, 0);");

                    break;
                case 4:
                    mydb.updateStateOfRoomByRelayPin(3, 0);
                    Log.i("My tag","mydb.updateStateOfRoomByRelayPin(3, 0);");

                    break;
                case 5:
                    mydb.updateStateOfRoomByRelayPin(0, 1);
                    Log.i("My tag","mydb.updateStateOfRoomByRelayPin(0, 1);");

                    break;
                case 6:
                    mydb.updateStateOfRoomByRelayPin(1, 1);
                    Cursor r = mydb.getData(1);
                    Log.i("My tag", "+" + r.getInt(r.getColumnIndex(DBHelper.ROOMS_COLUMN_STATE)));
                    Log.i("My tag","mydb.updateStateOfRoomByRelayPin(1, 1);");

                    break;
                case 7:
                    mydb.updateStateOfRoomByRelayPin(2, 1);
                    Log.i("My tag","mydb.updateStateOfRoomByRelayPin(2, 1);");

                    break;
                case 8:
                    mydb.updateStateOfRoomByRelayPin(3, 1);
                    Log.i("My tag","mydb.updateStateOfRoomByRelayPin(3, 1);");
                    break;
            }
            try{
                outputStream.write(value_to_send);
                Log.i("Kani", "write  home frag 570");
                Log.i("My tag","value_to_send = " + value_to_send);
                Toast.makeText(getContext(), "sending "+ value_to_send, Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Log.i("My tag", "try send in voice control");
                e.printStackTrace();
            }
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.home_frame, new HomeFragment()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            Log.i("My tag","update Home fragment in voice control");
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    @Override
    public void onInit(int arg0) {
        fab_voice_control.setEnabled(true);
    }
    //----------------------------



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
        //Close the Text to Speech Library
        if(tts != null) {

            tts.stop();
            tts.shutdown();
            Log.d("My tag", "TTS Destroyed");
        }super.onDestroyView();
        Log.i("My tag", "on DestroyView of home fragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("My tag", "on resume of home fragment");

//        final int id = ((MyBluetoothSocketApplication) getActivity().getApplication()).getRoom_id();
//        final int state = ((MyBluetoothSocketApplication) getActivity().getApplication()).getState();
//        long diff = ((MyBluetoothSocketApplication) getActivity().getApplication()).getTime_difference();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {//Do something after 100ms
//                mydb.updateStateOfRoom(id, state);
////                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit();
//            }
//        }, diff);

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
//    }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().moveTaskToBack(true);
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