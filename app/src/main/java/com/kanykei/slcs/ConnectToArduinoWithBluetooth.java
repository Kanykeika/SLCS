package com.kanykei.slcs;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ConnectToArduinoWithBluetooth extends Activity {
    //-----Bluetooth
    private TextView mStatusTv;
    private TextView mViewAvailableTv;
    private TextView mViewPairedTv;
    private TextView mNoAvailableDevicesTv;
    private ToggleButton mActivateBtn;
    private Button mScanBtn;
    private ProgressDialog mProgressDlg;
    private BluetoothAdapter mBluetoothAdapter;
    private String address = null;
    private ProgressDialog progress;
    private BluetoothSocket btSocket;
    private boolean isBtConnected = false;
    private ListView lv_paired;
    private ListView lv_scan;
    private ArrayList<String> mScannedDeviceNameList;
    private ArrayList<String> mScannedDeviceAddressList;
    private ArrayList<String> mPairedDeviceNameList;
    private ArrayList<String> mPairedDeviceAddressList;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);


        mStatusTv = (TextView) findViewById(R.id.tv_status);
        mViewAvailableTv = (TextView) findViewById(R.id.tv_view_available);
        mViewPairedTv = (TextView) findViewById(R.id.tv_view_paired);
        mNoAvailableDevicesTv = (TextView) findViewById(R.id.tv_no_available_devices);
        mActivateBtn = (ToggleButton) findViewById(R.id.btn_enable);
        mScanBtn = (Button) findViewById(R.id.btn_scan);
        lv_paired = (ListView) findViewById(R.id.paired_devices_list);
        lv_scan = (ListView) findViewById(R.id.available_devices_list);
        hideScan();
        mProgressDlg = new ProgressDialog(this);
        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                hideScan();
                mBluetoothAdapter.cancelDiscovery();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    showScan();
                    mScannedDeviceNameList = new ArrayList<String>();
                    mScannedDeviceAddressList = new ArrayList<String>();
                    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
                    ActivityCompat.requestPermissions(ConnectToArduinoWithBluetooth.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    mBluetoothAdapter.startDiscovery();
                    Log.i("My tag", "start discovery");
                }
            });

            mActivateBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                    if (isChecked) {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, 0);
                    }else {
                        mBluetoothAdapter.disable();
                        showDisabled();
                    }
                }
            });

            if (mBluetoothAdapter.isEnabled()) {
                mActivateBtn.setChecked(true);
                showEnabled();
            } else {
                mActivateBtn.setChecked(false);
                showDisabled();
            }
        }


    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
        Log.i("My tag","on pause connect to ard bt");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        Log.i("My tag","on destroy connect to ard bt");
        super.onDestroy();
    }

    @Override
    public void onStop() {
        unregisterReceiver(mReceiver);
        Log.i("My tag","on stop connect to ard bt");
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("My tag","on resume connect to ard bt");
        if (mBluetoothAdapter.isEnabled()) {
            mActivateBtn.setChecked(true);
            showEnabled();
        } else {
            mActivateBtn.setChecked(false);
            showDisabled();
        }
    }

    private void showEnabled() {
        mScanBtn.setEnabled(true);
        mViewPairedTv.setVisibility(View.VISIBLE);
        lv_paired.setVisibility(View.VISIBLE);
        mScanBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mScanBtn.setTextColor(getResources().getColor(android.R.color.white));
        Log.i("My tag", "show enabled");
    }

    private void showDisabled() {
        mScanBtn.setEnabled(false);
        hideScan();
        mViewPairedTv.setVisibility(View.INVISIBLE);
        lv_paired.setVisibility(View.INVISIBLE);
        mScanBtn.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        mScanBtn.setTextColor(Color.LTGRAY);
        Log.i("My tag", "show disabled");

    }

    private void showScan() {
        mViewAvailableTv.setVisibility(View.VISIBLE);
        lv_scan.setVisibility(View.VISIBLE);
        Log.i("My tag", "showScan");

    }

    private void hideScan() {
        mViewAvailableTv.setVisibility(View.INVISIBLE);
        mNoAvailableDevicesTv.setVisibility(View.INVISIBLE);
        lv_scan.setVisibility(View.INVISIBLE);
        Log.i("My tag", "hideScan");

    }

    private void showUnsupported() {
        mStatusTv.setText("Bluetooth is unsupported by this device");
        mActivateBtn.setVisibility(View.INVISIBLE);
        mScanBtn.setEnabled(false);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    msg("Enabled");
                    showEnabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mProgressDlg.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDlg.dismiss();
                try {
                    if (!mScannedDeviceNameList.isEmpty() && mScannedDeviceNameList != null ) {
                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item, mScannedDeviceNameList);
                        lv_scan.setAdapter(adapter);
                        lv_scan.setOnItemClickListener(myScannedListClickListener);
                    } else if(mBluetoothAdapter.isEnabled()) {
                        mNoAvailableDevicesTv.setVisibility(View.VISIBLE);
                        Log.i("My tag", "show not found");
                    }
                }catch (Exception e){
                    Log.e("My Tag",e.toString());
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mScannedDeviceNameList.add(device.getName());
                mScannedDeviceAddressList.add(device.getAddress());
                msg("Found device " + device.getName());
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if(resultCode == Activity.RESULT_OK){
                msg("Turned on");
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                mPairedDeviceNameList = new ArrayList<String>();
                mPairedDeviceAddressList = new ArrayList<String>();
                if (pairedDevices == null || pairedDevices.size() == 0) {
                    msg("No Paired Devices Found");
                } else {
                    for(BluetoothDevice bt : pairedDevices){
                        mPairedDeviceNameList.add(bt.getName());
                        mPairedDeviceAddressList.add(bt.getAddress());
                    }
                    msg("Showing Paired Devices");
                    final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item, mPairedDeviceNameList);
                    lv_paired.setAdapter(adapter);
                    lv_paired.setOnItemClickListener(myPairedListClickListener);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                msg("Could not turn on");
            }
        }
    }

    private AdapterView.OnItemClickListener myPairedListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address (17 chars)
            address = mPairedDeviceAddressList.get(arg2);
            new ConnectBT().execute();
        }
    };

    private AdapterView.OnItemClickListener myScannedListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address (17 chars)
            address = mScannedDeviceAddressList.get(arg2);
            new ConnectBT().execute();
        }
    };


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ConnectToArduinoWithBluetooth.this, "Connecting...", "Please wait!!!");  //show a progress dialog

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = mBluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                    // set Bluetooth socket to be global variable
                    ((MyBluetoothSocketApplication) getApplication()).setBtSocket(btSocket);
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP (Serial Port Profile)  Bluetooth? Try again.");
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
                Intent turnOn = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(turnOn);
            }
            progress.dismiss();
        }
    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
}
