package com.kanykei.slcs;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.support.design.widget.FloatingActionButton;
import android.view.View.OnClickListener;
import android.widget.Toast;


public class HomeFragment extends Fragment implements OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    private ListView obj;
    private DBHelper mydb;
    private Toolbar toolbar;
    private FloatingActionButton fab;



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
        getFragmentManager().beginTransaction().replace(R.id.home_frame, createRoomFragment).commit();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View myView = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = (Toolbar) myView.findViewById(R.id.toolbar);

        setHasOptionsMenu(true);

        fab = (FloatingActionButton) myView.findViewById(R.id.fab);
        fab.show();
        fab.setOnClickListener(this);

        mydb = DBHelper.getInstance(getContext());
        final ArrayList<Room> array_list = mydb.getAllRooms();

        RoomAdapter adapter = new RoomAdapter(myView.getContext(),array_list);
        obj = (ListView) myView.findViewById(R.id.listView);
        obj.setAdapter(adapter);

        obj.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", array_list.get(position).getId());
                CreateRoomFragment createRoomFragment = new CreateRoomFragment();
                createRoomFragment.setArguments(dataBundle);
                getFragmentManager().beginTransaction().replace(R.id.home_frame, createRoomFragment).commit();
            }
        });
        obj.setLongClickable(true);
        obj.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder aat = new AlertDialog.Builder(getContext());
                aat.setTitle(getString(R.string.delete_dialog) + "?")
                        .setMessage(getString(R.string.delete_dialog_sure) + array_list.get(position).getName() +"?")
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
                                mydb.deleteRoom(array_list.get(position).getId());
                                Toast.makeText(getContext(), getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                                getFragmentManager().beginTransaction().replace(R.id.home_frame, new HomeFragment()).commit(); //call it here to refresh listView upon delete
                            }
                        });
                AlertDialog art = aat.create();

                art.show();
                return true;

            }

        });

        return myView;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        inflater.inflate(R.menu.display_rooms, menu);
//        super.onCreateOptionsMenu(menu,inflater);
//    }

}