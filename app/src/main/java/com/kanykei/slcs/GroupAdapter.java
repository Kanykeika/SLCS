package com.kanykei.slcs;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
    public View getView(final int position, View view, final ViewGroup parent) {
        final ViewHolder  holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.group_listview, parent, false);
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
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int isCheckedInt = (isChecked) ? 1 : 0;
                    if (buttonView.isPressed()) {
                        mydb.updateStateOfGroup(groupsArrayList.get(position).getId(), isCheckedInt);
                        roomsArrayList = mydb.getRoomsByGroupId(groupsArrayList.get(position).getId());
                        try {
                            if (isChecked) { // to turn on
                                for (int i = 0; i < roomsArrayList.size(); i++) {
                                    int room_id = roomsArrayList.get(i).getId();
                                    int write = 0;
                                    Log.i("My tag", "i=" + i);
                                    Log.i("My tag", "roomsArrayList.size()=" + roomsArrayList.size());
                                    if (mydb.getRelayPinById(room_id) == 0) {
                                        write = 5;
                                        Log.i("My tag", "room id[i]=" + room_id);
                                        Log.i("My tag", "write 5 Group adapter 102");

                                    } else if (mydb.getRelayPinById(room_id) == 1) {
                                        write = 6;
                                        Log.i("My tag", "room id[i]=" + room_id);
                                        Log.i("My tag", "write 6 Group adapter 107");
                                    } else if (mydb.getRelayPinById(room_id) == 2) {
                                        write = 7;
                                        Log.i("My tag", "room id[i]=" + room_id);
                                        Log.i("My tag", "write 7 Group adapter 111");
                                    } else if (mydb.getRelayPinById(room_id) == 3) {
                                        write = 8;
                                        Log.i("My tag", "room id[i]=" + room_id);
                                        Log.i("My tag", "write 8 Group adapter 115");
                                    }
                                    outputStream.write(write);
                                    mydb.updateStateOfRoom(room_id, 1);

                                }
                            } else { // to turn off
                                for (int i = 0; i < roomsArrayList.size(); i++) {
//                                    mydb.updateStateOfRoom(roomsArrayList.get(i).getId(), isCheckedInt);
                                    Log.i("My tag", "i=" + i);
                                    Log.i("My tag", "roomsArrayList.size()=" + roomsArrayList.size());
                                    int room_id = roomsArrayList.get(i).getId();
                                    int write = 0;
                                    if (mydb.getRelayPinById(room_id) == 0) {
                                        write = 1;
                                        Log.i("My tag", "room id[i]=" + room_id);
                                        Log.i("My tag", "write 1 Group adapter 125");
                                    } else if (mydb.getRelayPinById(room_id) == 1) {
                                        write = 2;
                                        Log.i("My tag", "room id[i]=" + room_id);
                                        Log.i("My tag", "write 2 Group adapter 129");
                                    } else if (mydb.getRelayPinById(room_id) == 2) {
                                        write = 3;
                                        Log.i("My tag", "room id[i]=" + room_id);
                                        Log.i("My tag", "write 3 Group adapter 133");
                                    } else if (mydb.getRelayPinById(room_id) == 3) {
                                        write = 4;
                                        Log.i("My tag", "room id[i]=" + room_id);
                                        Log.i("My tag", "write 4 Group adapter 137");
                                    }
                                    outputStream.write(write);
                                    mydb.updateStateOfRoom(room_id, 0);

                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Broken pipe. Reconnect with Bluetooth", Toast.LENGTH_LONG).show();
                            btSocket = ((MyBluetoothSocketApplication) getContext().getApplicationContext()).getBtSocket();
                            try {
                                outputStream = btSocket.getOutputStream();
                                Log.i("Kani", "get  Group adapter 147");
                                inputStream = btSocket.getInputStream();
                                outputStream.close();
                                Log.i("Kani", "close Group adapter 150");
                                inputStream.close();
                                btSocket.close();
                                Log.i("My tag", "Bluetooth socket closed");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            Intent intent = new Intent(getContext(), ConnectToArduinoWithBluetooth.class);
                            getContext().startActivity(intent);
                        }


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
