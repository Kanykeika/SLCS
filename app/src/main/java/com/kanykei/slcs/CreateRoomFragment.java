package com.kanykei.slcs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;


public class CreateRoomFragment extends Fragment{

    private EditText inputName;
    private TextInputLayout inputLayoutName;
    private DBHelper mydb;
    int id_To_Update = 0;

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

        inputLayoutName = (TextInputLayout) createRoomView.findViewById(R.id.input_layout_name);
        inputName = (EditText) createRoomView.findViewById(R.id.input_name);
        String[] relay_pins = { "IN0", "IN1", "IN2", "IN3" };
        Spinner spinner = (Spinner) createRoomView.findViewById(R.id.spinner_relay_pins);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_item, relay_pins);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0); // get relay pin from db
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        mydb = DBHelper.getInstance(getContext());

        Bundle extras = getArguments();
        if(extras != null) {
            int Value = extras.getInt("id");
            if(Value > 0){
                //means this is the view part not the add room part.
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();
                String col_name = rs.getString(rs.getColumnIndex(DBHelper.ROOMS_COLUMN_NAME));
                String col_id = rs.getString(rs.getColumnIndex(DBHelper.ROOMS_COLUMN_ID));
                if (!rs.isClosed())  {
                    rs.close();
                }
                Button b = (Button)createRoomView.findViewById(R.id.btn_save);
                b.setVisibility(View.VISIBLE);
                inputName.setText((CharSequence)col_name);
                inputLayoutName.setError(col_id);
                inputName.setEnabled(true);
                inputName.setFocusableInTouchMode(true);
                inputName.setClickable(true);
            }
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.deleteRoom).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mydb.deleteRoom(id_To_Update);
                        Toast.makeText(getContext(), R.string.success_delete, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
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
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void run(View view) {
        Bundle extras = getArguments();
        if(extras != null) {
            int Value = extras.getInt("id");
            if(Value > 0){
                if(!validateName()) {
                    return;
                }
                else if(mydb.updateRoom(id_To_Update,inputName.getText().toString())){
                    Toast.makeText(getContext(), R.string.updated, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                } else{
                    inputLayoutName.setError(getString(R.string.err_msg_duplicate_name));
                    requestFocus(inputName);
                    Toast.makeText(getContext(), R.string.not_updated, Toast.LENGTH_SHORT).show();
                    return;
                }
            } else{
                if (!validateName()) {
                    return;
                }
                else if (mydb.insertRoom(inputName.getText().toString())) {
                    Toast.makeText(getContext(), R.string.done, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    inputLayoutName.setError(getString(R.string.err_msg_duplicate_name));
                    requestFocus(inputName);
                    Toast.makeText(getContext(), R.string.not_done, Toast.LENGTH_SHORT).show();
                    return;
                }

            }
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


