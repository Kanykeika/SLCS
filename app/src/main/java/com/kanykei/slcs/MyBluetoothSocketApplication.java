package com.kanykei.slcs;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

public class MyBluetoothSocketApplication extends Application {
    BluetoothSocket btSocket;
    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
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
