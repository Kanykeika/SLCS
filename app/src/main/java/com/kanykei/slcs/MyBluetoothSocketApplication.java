package com.kanykei.slcs;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.support.v7.widget.RecyclerView;

public class MyBluetoothSocketApplication extends Application {
    BluetoothSocket btSocket;
    long time_difference;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    int counter = 0;

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
