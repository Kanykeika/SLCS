package com.kanykei.slcs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RoomAdapter extends ArrayAdapter<Room> {

    private final Context context;
    private final ArrayList<Room> roomsArrayList;
    private DBHelper mydb;
    private SparseBooleanArray mSelectedItemsIds;
    LayoutInflater inflater;
    // Container Class for item
    private class ViewHolder {
        TextView labelView;
        TextView valueView;
        ToggleButton toggleButton;
    }
    public RoomAdapter(Context context, int resourceId, ArrayList<Room> itemsArrayList) {

        super(context, resourceId, itemsArrayList);

        this.context = context;
        this.roomsArrayList = itemsArrayList;
        mSelectedItemsIds = new SparseBooleanArray();
        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder  holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview, null);
            // Locate the TextViews in  listview_item.xml
            holder.labelView = (TextView) view.findViewById(R.id.room_id);
            holder.valueView = (TextView) view.findViewById(R.id.room_name);
            holder.toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)  view.getTag();
        }
        // Capture position and set to the  TextViews
        //  Set the text for textView
        holder.labelView.setText(String.valueOf(roomsArrayList.get(position).getId()));
        holder.valueView.setText(String.valueOf(roomsArrayList.get(position).getName()));

        mydb = DBHelper.getInstance(getContext());

        // 5. Set listener for toggle button
        Cursor res = mydb.getData(roomsArrayList.get(position).getId());
        if(res.getCount() != 0) {
            int state = res.getInt(res.getColumnIndex(DBHelper.ROOMS_COLUMN_STATE));
            if (state == 1) {
                holder.toggleButton.setChecked(true);
            } else {
                holder.toggleButton.setChecked(false);
            }
            holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                    int isCheckedInt = (isChecked) ? 1 : 0;
                    mydb.updateStateOfRoom(roomsArrayList.get(position).getId(), isCheckedInt);
                }
            });
        }

        return view;

    }


    @Override
    public void remove(Room object) {
        roomsArrayList.remove(object);
        notifyDataSetChanged();
    }

    public List<Room> getRooms() {
        return roomsArrayList;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

}
