package com.kanykei.slcs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CreateGroupFragment extends Fragment{

    private EditText inputName;
    private TextInputLayout inputLayoutName;
    private DBHelper mydb;
    int id_To_Update = 0;
    TextView toolbar_title;
    private ListView listView;
    ArrayList<Room> rooms_array_list;
    ArrayList<Integer> room_ids;
    public CreateGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View createGroupView = inflater.inflate(R.layout.fragment_create_group, container, false);
        Log.i("My tag", "on createview CreateGroupFragment");
        toolbar_title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        mydb = DBHelper.getInstance(getContext());
        final Bundle extras = getArguments();
        if(extras != null) {
            room_ids = extras.getIntegerArrayList("room_ids");
            rooms_array_list = mydb.getRoomsByIds(room_ids);
        }
        inputLayoutName = (TextInputLayout) createGroupView.findViewById(R.id.input_layout_name);
        inputName = (EditText) createGroupView.findViewById(R.id.input_name);
        final CreateGroupAdapter adapter = new CreateGroupAdapter(createGroupView.getContext(),R.layout.create_group_listview, rooms_array_list);
        listView = (ListView) createGroupView.findViewById(R.id.listViewGroup);
        listView.setAdapter(adapter);
        toolbar_title.setText(R.string.create_group);

        Button b = (Button)createGroupView.findViewById(R.id.btn_save);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(extras != null) {
                    // insert a new group
                    if(!validateName()) {
                    }
                    else if (!mydb.isDuplicateGroup(inputName.getText().toString())) {
                        mydb.insertGroup(inputName.getText().toString(), room_ids);
                        Toast.makeText(getContext(), R.string.done_group, Toast.LENGTH_SHORT).show();
                        getFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit();
                    } else {
                        inputLayoutName.setError(getString(R.string.err_msg_duplicate_group));
//                            requestFocus(inputName);
                        Toast.makeText(getContext(), R.string.not_done_group, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return createGroupView;
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
                        mydb.deleteGroup(id_To_Update);
                        Toast.makeText(getContext(), R.string.success_delete, Toast.LENGTH_SHORT).show();
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


