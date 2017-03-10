package com.kanykei.slcs;

import android.content.Intent;
import android.os.Bundle;
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

public class HomeFragment extends Fragment implements OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    private ListView obj;
    DBHelper mydb;
    Toolbar toolbar;
    private FloatingActionButton fab;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = (Toolbar) myView.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);

        fab = (FloatingActionButton) myView.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mydb = new DBHelper(getActivity());
        ArrayList<String> array_list = mydb.getAllRooms();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(myView.getContext(),R.layout.listview, array_list);

        obj = (ListView) myView.findViewById(R.id.listView);
        obj.setAdapter(arrayAdapter);
        obj.setId(R.id.viewpager);
        obj.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                int id_To_Search = arg2 + 1;

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);

                Intent intent = new Intent(getActivity().getApplicationContext(),NewRoomActivity.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
        return myView;
    }

}