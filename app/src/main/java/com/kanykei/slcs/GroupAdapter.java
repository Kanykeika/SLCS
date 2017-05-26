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

public class GroupAdapter extends ArrayAdapter<Group> {

    private final Context context;
    private final ArrayList<Group> groupsArrayList;
    private ArrayList<Room> roomsArrayList;
    private DBHelper mydb;
    private SparseBooleanArray mSelectedItemsIds;
    LayoutInflater inflater;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private BluetoothSocket btSocket;
    // Container Class for item
    private class ViewHolder {
        TextView labelView;
        TextView valueView;
        ToggleButton toggleButton;
    }
    public GroupAdapter(Context context, int resourceId, ArrayList<Group> itemsArrayList) {

        super(context, resourceId, itemsArrayList);

        this.context = context;
        this.groupsArrayList = itemsArrayList;
        mSelectedItemsIds = new SparseBooleanArray();
        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder  holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.group_listview, null);
            // Locate the TextViews in  listview_item.xml
            holder.labelView = (TextView) view.findViewById(R.id.group_id);
            holder.valueView = (TextView) view.findViewById(R.id.group_name);
            holder.toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)  view.getTag();
        }
        // Capture position and set to the  TextViews
        //  Set the text for textView
        holder.labelView.setText(String.valueOf(groupsArrayList.get(position).getId()));
        holder.valueView.setText(String.valueOf(groupsArrayList.get(position).getName()));

        mydb = DBHelper.getInstance(getContext());
        btSocket = ((MyBluetoothSocketApplication) context.getApplicationContext()).getBtSocket();
        try{
            outputStream = btSocket.getOutputStream();
            Log.i("Kani", "get stream Group adapter 74");
        }catch (Exception e){
            Toast.makeText(getContext(),"Failed to getOutputStream",Toast.LENGTH_LONG);
            e.printStackTrace();
        }
        // 5. Set listener for toggle button
        Cursor res = mydb.getGroupData(groupsArrayList.get(position).getId());

        if(res.getCount() != 0) {
            int state = res.getInt(res.getColumnIndex(DBHelper.GROUP_COLUMN_STATE));
            if (state == 1) {
                holder.toggleButton.setChecked(true);
            } else {
                holder.toggleButton.setChecked(false);
            }
            holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                    int isCheckedInt = (isChecked) ? 1 : 0;
                    mydb.updateStateOfGroup(groupsArrayList.get(position).getId(), isCheckedInt);
                    roomsArrayList = mydb.getRoomsByGroupId(groupsArrayList.get(position).getId());
                        try{
                            if(isChecked){ // to turn on
                                for(int i=0; i<roomsArrayList.size();i++){
//                                    mydb.updateStateOfRoom(roomsArrayList.get(i).getId(),isCheckedInt);
                                    if( mydb.getRelayPinById(roomsArrayList.get(i).getId()) == 0){
                                        outputStream.write(5);
                                        Log.i("My tag", "room id[i]=" + roomsArrayList.get(i).getId());
                                        Log.i("Kani", "write Group adapter 102");

                                    }
                                    else if( mydb.getRelayPinById(roomsArrayList.get(i).getId()) == 1){
                                        outputStream.write(6);
                                        Log.i("Kani", "write Group adapter 107");
                                    }
                                    else if( mydb.getRelayPinById(roomsArrayList.get(i).getId()) == 2){
                                        outputStream.write(7);
                                        Log.i("Kani", "write Group adapter 111");
                                    }
                                    else if( mydb.getRelayPinById(roomsArrayList.get(i).getId()) == 3){
                                        outputStream.write(8);
                                        Log.i("Kani", "write Group adapter 115");
                                    }
                                }

                            }else{ // to turn off
                                for(int i=0; i<roomsArrayList.size();i++) {
//                                    mydb.updateStateOfRoom(roomsArrayList.get(i).getId(), isCheckedInt);

                                    if (mydb.getRelayPinById(roomsArrayList.get(i).getId()) == 0) {
                                        outputStream.write(1);
                                        Log.i("Kani", "write Group adapter 125");
                                    }
                                    else if (mydb.getRelayPinById(roomsArrayList.get(i).getId()) == 1) {
                                        outputStream.write(2);
                                        Log.i("Kani", "write Group adapter 129");
                                    }
                                    else if (mydb.getRelayPinById(roomsArrayList.get(i).getId()) == 2) {
                                        outputStream.write(3);
                                        Log.i("Kani", "write Group adapter 133");
                                    }
                                    else if (mydb.getRelayPinById(roomsArrayList.get(i).getId()) == 3) {
                                        outputStream.write(4);
                                        Log.i("Kani", "write Group adapter 137");
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Broken pipe. Reconnect with Bluetooth", Toast.LENGTH_LONG).show();
                            btSocket = ((MyBluetoothSocketApplication) getContext().getApplicationContext()).getBtSocket();
                            try{
                                outputStream = btSocket.getOutputStream();
                                Log.i("Kani", "get  Group adapter 147");
                                inputStream = btSocket.getInputStream();
                                outputStream.close();
                                Log.i("Kani", "close Group adapter 150");
                                inputStream.close();
                                btSocket.close();
                                Log.i("My tag", "Bluetooth socket closed");
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                            Intent intent = new Intent(getContext(), ConnectToArduinoWithBluetooth.class);
                            getContext().startActivity(intent);
                        }


                }
            });
        }
        if (!res.isClosed())  {
            res.close();
        }
        return view;

    }


    @Override
    public void remove(Group object) {
        groupsArrayList.remove(object);
        notifyDataSetChanged();
    }

    public List<Group> getRooms() {
        return groupsArrayList;
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
