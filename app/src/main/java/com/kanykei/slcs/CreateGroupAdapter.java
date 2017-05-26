package com.kanykei.slcs;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.kanykei.slcs.R.id.toggleButton;

public class CreateGroupAdapter extends ArrayAdapter<Room> {

    private final Context context;
    private final ArrayList<Room> roomsArrayList;
    LayoutInflater inflater;

    // Container Class for item

        TextView labelView;
        TextView valueView;

    public CreateGroupAdapter(Context context, int resourceId, ArrayList<Room> itemsArrayList) {

        super(context, resourceId, itemsArrayList);

        this.context = context;
        this.roomsArrayList = itemsArrayList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        view = inflater.inflate(R.layout.create_group_listview, null);
        // Locate the TextViews in  listview_item.xml
        labelView = (TextView) view.findViewById(R.id.position);
        valueView = (TextView) view.findViewById(R.id.room_name);

        // Capture position and set to the  TextViews
        //  Set the text for textView
        labelView.setText(String.valueOf(position+1));
        valueView.setText(String.valueOf(roomsArrayList.get(position).getName()));

        return view;

    }


    public List<Room> getRooms() {
        return roomsArrayList;
    }
}
