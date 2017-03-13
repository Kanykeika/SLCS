package com.kanykei.slcs;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RoomAdapter extends ArrayAdapter<Room> {

    private final Context context;
    private final ArrayList<Room> roomsArrayList;

    public RoomAdapter(Context context, ArrayList<Room> itemsArrayList) {

        super(context, R.layout.listview, itemsArrayList);

        this.context = context;
        this.roomsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.listview, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.room_id);
        TextView valueView = (TextView) rowView.findViewById(R.id.room_name);

        // 4. Set the text for textView
        labelView.setText(String.valueOf(roomsArrayList.get(position).getId()));
        valueView.setText(String.valueOf(roomsArrayList.get(position).getName()));

        // 5. return rowView
        return rowView;
    }
}
