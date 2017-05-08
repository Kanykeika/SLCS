package com.kanykei.slcs;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class HomeFragment extends Fragment implements TextToSpeech.OnInitListener, OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }
// recognizer
    TextToSpeech tts;
    Spinner spinnerResult;
    OutputStream outputStream;
    InputStream inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    BluetoothSocket btSocket;
    private static final int RQS_RECOGNITION = 1;

    // CRUD rooms
    private ListView obj;
    private DBHelper mydb;
    private FloatingActionButton fab;
    private Button fab_voice_control;
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



        //-----------------------
        //speech recognition code

        boolean voice_control = loadVoiceControl(false);


        fab_voice_control = (Button) myView.findViewById(R.id.fab_voice_control);

        if(!voice_control){
            fab_voice_control.setVisibility(View.INVISIBLE);
        }else{
            fab_voice_control.setVisibility(View.VISIBLE);
            btSocket = ((MyBluetoothSocketApplication) getActivity().getApplication()).getBtSocket(); // get global Bluetooth Socket variable from application class
            try{
                outputStream = btSocket.getOutputStream();
                inputStream = btSocket.getInputStream();
                beginListenForData();
                beginListenForData1();
            }catch (Exception e){
                Toast.makeText(getContext(),"Failed to getOutputStream",Toast.LENGTH_LONG);
                Toast.makeText(getContext(),"Failed to getInputStream",Toast.LENGTH_LONG);
                e.printStackTrace();
            }
            fab_voice_control.setOnClickListener(startRecognizerOnClickListener);
            spinnerResult = (Spinner) myView.findViewById(R.id.result);
            tts = new TextToSpeech(getContext(), this);
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


    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = inputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Toast.makeText(getContext(), data, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    final int handlerState = 0;                        //used to identify handler message
    private StringBuilder recDataString = new StringBuilder();

    void beginListenForData1(){
        Handler bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        Toast.makeText(getContext(), "Data Received = " + dataInPrint, Toast.LENGTH_SHORT).show();
                        int dataLength = dataInPrint.length();                          //get length of data received
                        Toast.makeText(getContext(), "String Length = " + String.valueOf(dataLength), Toast.LENGTH_SHORT).show();

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 2);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(3, 4);            //same again...
                            String sensor2 = recDataString.substring(5, 6);
                            String sensor3 = recDataString.substring(7, 8);

                            Toast.makeText(getContext(), " Sensor 0  = " + sensor0 , Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), " Sensor 1  = " + sensor1 , Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), " Sensor 2  = " + sensor2 , Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), " Sensor 3  = " + sensor3 , Toast.LENGTH_SHORT).show();
                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };
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
            //Toast.makeText(MainActivity.this, selectedResult, Toast.LENGTH_LONG).show();
            //tts.speak(selectedResult, TextToSpeech.QUEUE_ADD, null);
            //String answer = spinnerResult.getSelectedItem().toString();
            int b = '4';
            if(selectedResult.toLowerCase().equals("turn on kitchen") || selectedResult.toLowerCase().equals("включить kitchen")){
                b = '5';
            }else
            if(selectedResult.toLowerCase().equals("turn off kitchen") || selectedResult.toLowerCase().equals("включить kitchen")){
                b = '1'; // if b == ReLAY_1 // then go / turn off
            }else
            if(selectedResult.toLowerCase().equals("turn on room") || selectedResult.toLowerCase().equals("включить kitchen")){
                b = '3';
            }else
            if(selectedResult.toLowerCase().equals("turn off room") || selectedResult.toLowerCase().equals("включить kitchen")){
                b = '4';
            }else
            if(selectedResult.toLowerCase().equals("turn on") || selectedResult.toLowerCase().equals("включить")){
                b = '5';
            }

            try{
                outputStream.write(b);
                Toast.makeText(getContext(), "sending "+b, Toast.LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
            }
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