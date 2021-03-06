package com.kanykei.slcs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CreateRoomFragment extends Fragment {

    private EditText inputName;
    private TextInputLayout inputLayoutName;
    private DBHelper mydb;
    int id_To_Update = 0;
    int relay_pin;
    TextView toolbar_title;

    public CreateRoomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View createRoomView = inflater.inflate(R.layout.fragment_create_room, container, false);
        toolbar_title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        mydb = DBHelper.getInstance(getActivity());
        inputLayoutName = (TextInputLayout) createRoomView.findViewById(R.id.input_layout_name);
        inputName = (EditText) createRoomView.findViewById(R.id.input_name);
        final String[] relay_pins = { "K1", "K2", "K3", "K4" };
        Spinner spinner = (Spinner) createRoomView.findViewById(R.id.spinner_relay_pins);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, relay_pins) {
            // Disable click item where relay pin is already taken
            @Override
            public boolean isEnabled(int position) {
                // TODO Auto-generated method stub
                Cursor rs = mydb.getDataByRelayPin(getArguments().getInt("id"),position);
                if(rs.getCount() != 0){
                    int relay = rs.getInt(rs.getColumnIndex(DBHelper.ROOMS_COLUMN_RELAY_PIN));
                    if (!rs.isClosed())  {
                        rs.close();
                    }
                    return false;
                }
                if (!rs.isClosed())  {
                    rs.close();
                }
                return true;
            }
            ArrayList<Room> array_list = mydb.getAllRooms();

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                // TODO Auto-generated method stub
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                Cursor rs = mydb.getDataByRelayPin(getArguments().getInt("id"),position);
                if(rs.getCount() != 0){
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }

                if (!rs.isClosed())  {
                    rs.close();
                }

                return mView;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        for(int i=0; i<relay_pins.length; i++){
            Cursor rs = mydb.getDataByRelayPin(getArguments().getInt("id"),i);
            if (rs.getCount() != 0) {
                if (!rs.isClosed())  {
                    rs.close();
                }
            } else {
                spinner.setSelection(adapter.getPosition(relay_pins[i]));
                if (!rs.isClosed())  {
                    rs.close();
                }
                break;
            }

        }

        Bundle extras = getArguments();
        if(extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                toolbar_title.setText(R.string.edit_room);

                //means this is the view room part not the add room part.
                Cursor rs = mydb.getData(Value);
                rs.moveToFirst();
                relay_pin = rs.getInt(rs.getColumnIndex(DBHelper.ROOMS_COLUMN_RELAY_PIN));
                spinner.setSelection(relay_pin); // get relay pin from db
                Log.i("My tag", "relay_pin from db = " + relay_pin);
                id_To_Update = Value;
                String col_name = rs.getString(rs.getColumnIndex(DBHelper.ROOMS_COLUMN_NAME));
                String col_id = rs.getString(rs.getColumnIndex(DBHelper.ROOMS_COLUMN_ID));
                if (!rs.isClosed())  {
                    rs.close();
                }

                inputName.setText(col_name);
                inputName.setFocusableInTouchMode(true);
                inputName.setClickable(true);
            }else{
                toolbar_title.setText(R.string.create_room);
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                relay_pin = arg2;
                Log.i("My tag", "relay_pin = " + relay_pin);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        Button b = (Button)createRoomView.findViewById(R.id.btn_save);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getArguments();
                if(extras != null) {
                    int Value = extras.getInt("id");
                    if(Value > 0){ // update existing room
                        if(!validateName()) {
                        }
                        else if(mydb.updateRoom(id_To_Update,inputName.getText().toString(), relay_pin)){
                            Toast.makeText(getActivity(), R.string.updated, Toast.LENGTH_SHORT).show();
                            getFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit();

                        } else{
                            inputLayoutName.setError(getString(R.string.err_msg_duplicate_name));
//                            requestFocus(inputName);
                            Toast.makeText(getActivity(), R.string.not_updated, Toast.LENGTH_SHORT).show();
                        }
                    } else{  // insert a new room
                        if(!validateName()) {
                        }
                        else if (mydb.insertRoom(inputName.getText().toString(), relay_pin)) {
                            Toast.makeText(getActivity(), R.string.done_room, Toast.LENGTH_SHORT).show();
                            getFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit();

                        } else {
                            inputLayoutName.setError(getString(R.string.err_msg_duplicate_name));
//                            requestFocus(inputName);
                            Toast.makeText(getActivity(), R.string.not_done_room, Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }
        });
        return createRoomView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Bundle extras = getArguments();
        if(extras != null) {
            int Value = extras.getInt("id");
            if(Value > 0){
                inflater.inflate(R.menu.display_rooms, menu);
            }
        }

        super.onCreateOptionsMenu(menu,inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.Delete_Room:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.deleteRoom).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mydb.deleteRoom(id_To_Update);
                        Toast.makeText(getActivity(), R.string.success_delete, Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit();
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog d = builder.create();
                d.setTitle(R.string.deleteRoomTitle);
                d.show();

                return true;
            case android.R.id.home:
                toolbar_title.setText(R.string.app_name);
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


}


