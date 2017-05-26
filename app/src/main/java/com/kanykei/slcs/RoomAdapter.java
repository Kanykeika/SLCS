package com.kanykei.slcs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class RoomAdapter extends ArrayAdapter<Room> {

    private final Context context;
    private final ArrayList<Room> roomsArrayList;
    private DBHelper mydb;
    private SparseBooleanArray mSelectedItemsIds;
    LayoutInflater inflater;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;
    private BluetoothSocket btSocket;
    // Container Class for item
    public class ViewHolder {
        TextView labelView;
        TextView valueView;
        ToggleButton toggleButton;
        ToggleButton getToggleBtn(){
            return toggleButton;
        }
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
        holder.labelView.setText(String.valueOf(position+1));
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
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int isCheckedInt = (isChecked) ? 1 : 0;
                    if(buttonView.isPressed()) {
                        mydb.updateStateOfRoom(roomsArrayList.get(position).getId(), isCheckedInt);

                        try {
                            btSocket = ((MyBluetoothSocketApplication) context.getApplicationContext()).getBtSocket();
                            try{
                                outputStream = btSocket.getOutputStream();
                                Log.i("Kani", "get room adapter 92");
                            }catch (Exception e){
                                Toast.makeText(getContext(),"Failed to getOutputStream",Toast.LENGTH_LONG);
                                e.printStackTrace();
                            }
                            if (isCheckedInt == 1) { // to turn on
                                if (roomsArrayList.get(position).getRelayPin() == 0) {
                                    outputStream.write(5);
                                    Log.i("Kani", "write room adapter 97");
                                } else if (roomsArrayList.get(position).getRelayPin() == 1) {
                                    outputStream.write(6);
                                    Log.i("Kani", "write room adapter 100");
                                } else if (roomsArrayList.get(position).getRelayPin() == 2) {
                                    outputStream.write(7);
                                    Log.i("Kani", "write room adapter 103");
                                } else if (roomsArrayList.get(position).getRelayPin() == 3) {
                                    outputStream.write(8);
                                    Log.i("Kani", "write room adapter 106");
                                }
                            } else { // to turn off
                                if (roomsArrayList.get(position).getRelayPin() == 0) {
                                    outputStream.write(1);
                                    Log.i("Kani", "write room adapter 111");
                                } else if (roomsArrayList.get(position).getRelayPin() == 1) {
                                    outputStream.write(2);
                                    Log.i("Kani", "write room adapter 114");
                                } else if (roomsArrayList.get(position).getRelayPin() == 2) {
                                    outputStream.write(3);
                                    Log.i("Kani", "write room adapter 117");
                                } else if (roomsArrayList.get(position).getRelayPin() == 3) {
                                    outputStream.write(4);
                                    Log.i("Kani", "write room adapter 120");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Broken pipe. Reconnect with Bluetooth", Toast.LENGTH_LONG).show();
                            btSocket = ((MyBluetoothSocketApplication) getContext().getApplicationContext()).getBtSocket();
                            try {
                                outputStream = btSocket.getOutputStream();
                                Log.i("Kani", "get room adapter 129");
                                inputStream = btSocket.getInputStream();
                                outputStream.close();
                                Log.i("Kani", "close room adapter 132");
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
