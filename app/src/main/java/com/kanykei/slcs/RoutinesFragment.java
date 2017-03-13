package com.kanykei.slcs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class RoutinesFragment extends Fragment{
    private DBHelper mydb;
    public RoutinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View routine_view = inflater.inflate(R.layout.fragment_routines, container, false);
        mydb = new DBHelper(getActivity());
        SQLiteDatabase db = mydb.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from rooms", null );
        res.moveToFirst();
//        TextView tv_id = (TextView) routine_view.findViewById(R.id.routine_id);
//        TextView tv_name = (TextView) routine_view.findViewById(R.id.routine_name);
        while(!res.isAfterLast()){
//            tv_id.setText(String.valueOf());
            System.out.println(res.getInt(res.getColumnIndex("id")));
            System.out.println(res.getString(res.getColumnIndex("name")));
//            tv_name.setText(String.valueOf(res.getColumnIndex("name")));
            res.moveToNext();
        }

        res =  db.rawQuery( "select * from rooms where id = 1", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            System.out.println(res.getInt(res.getColumnIndex("id")));
            System.out.println(res.getString(res.getColumnIndex("name")));
            res.moveToNext();
        }
        res =  db.rawQuery( "select * from rooms where id = 0", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            System.out.println(res.getInt(res.getColumnIndex("id")));
            System.out.println(res.getString(res.getColumnIndex("name")));
            res.moveToNext();
        }
        if (!res.isClosed())  {
            res.close();
        }
        return routine_view;
    }

}