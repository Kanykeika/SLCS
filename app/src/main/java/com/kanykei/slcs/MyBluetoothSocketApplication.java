package com.kanykei.slcs;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

public class MyBluetoothSocketApplication extends Application {
    BluetoothSocket btSocket;
    long time_difference;
    int room_id;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    int state;

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }
    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }

    public long getTime_difference() {
        return time_difference;
    }
    public void setTime_difference(long time_difference) {
        this.time_difference = time_difference;
    }

    public int getRoom_id() {
        return room_id;
    }
    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    private static MyBluetoothSocketApplication singleton;
    public static MyBluetoothSocketApplication getInstance() {
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
