package com.kanykei.slcs;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class HomeFragment extends Fragment implements OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    private ListView obj;
    DBHelper mydb;
    Toolbar toolbar;
    private FloatingActionButton fab;
    private TextView roomId;
    private TextView roomName;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("id", 0);

        Intent intent = new Intent(getActivity().getApplicationContext(),NewRoomActivity.class);
        intent.putExtras(dataBundle);

        startActivity(intent);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View myView = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = (Toolbar) myView.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);

        fab = (FloatingActionButton) myView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mydb = new DBHelper(getActivity());
        final ArrayList<Room> array_list = mydb.getAllRooms();

        RoomAdapter adapter = new RoomAdapter(myView.getContext(),array_list);

        obj = (ListView) myView.findViewById(R.id.listView);
        obj.setAdapter(adapter);

        obj.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", array_list.get(position).getId());
                Intent intent = new Intent(getActivity().getApplicationContext(),NewRoomActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
        obj.setLongClickable(true);
        obj.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder aat = new AlertDialog.Builder(getContext());
                aat.setTitle("Delete?")
                        .setMessage("Are you sure you want to delete " + array_list.get(position).getName() +"?")
                        .setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mydb.deleteRoom(array_list.get(position).getId());
                                Toast.makeText(getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
                                startActivity(intent);//call it here to refresh listView upon delete
                            }
                        });
                AlertDialog art = aat.create();

                art.show();
                return true;

            }

        });
        return myView;
    }

}