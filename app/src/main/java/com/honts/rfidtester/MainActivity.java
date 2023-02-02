package com.honts.rfidtester;

/*
RFID Tested app for IH40 and IH45 readers
master branch contains the SDK (version 1.1.0) for IH40 RFID Reader

For BT connection see scanAndConnect() Method , then do not forget to call createReader method
USB connection is automaticall established, we need to call createReader method.

 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.honeywell.rfidservice.EventListener;
import com.honeywell.rfidservice.RfidManager;
import com.honeywell.rfidservice.TriggerMode;
import com.honeywell.rfidservice.rfid.OnTagReadListener;
import com.honeywell.rfidservice.rfid.RfidReader;
import com.honeywell.rfidservice.rfid.RfidReaderException;
import com.honeywell.rfidservice.rfid.TagAdditionData;
import com.honeywell.rfidservice.rfid.TagReadData;
import com.honeywell.rfidservice.rfid.TagReadOption;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements EventListener, OnTagReadListener {

    private static final String TAG = "RFTesterB-MainActivity";

    private RfidManager rfidMgr;
    private ExecutorService executorService;
    private BluetoothAdapter bluetoothAdapter;
    private RfidReader mRfidReader;

    private Button connectBtn;
    private Button createBtn;
    private Button readBtn;
    private Button bluetoohtScanandConnectBtn;
    private Button stopReadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkPermissions();

        RfidManager.create(this, new RfidManager.CreatedCallback() {
            @Override
            public void onCreated(RfidManager rfidManager) {
                rfidMgr = rfidManager;
                rfidMgr.addEventListener(MainActivity.this);
            }
        });

        connectBtn = findViewById(R.id.connect);
        createBtn = findViewById(R.id.create);
        readBtn = findViewById(R.id.read);
        bluetoohtScanandConnectBtn = findViewById(R.id.btScanandConnect);
        stopReadBtn = findViewById(R.id.stopRead);

        bluetoohtScanandConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanAndConnect();
            }
        });


        execute();


    }

    // below method represents how to connect to RFID reader via BT
    
    @SuppressLint("MissingPermission")
    public void scanAndConnect() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.startLeScan(btScanCallback);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bluetoothAdapter.stopLeScan(btScanCallback);
        rfidMgr.connect("0C:23:69:19:85:07");

    }

    private void execute() {
        executorService = Executors.newFixedThreadPool(1);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                connectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rfidMgr != null) {
                            Log.d(TAG, "onClick: it is not null");
                        }

                        Log.d(TAG, "onClick: attached device is " + rfidMgr.getAttachedUsbDevice());
                        rfidMgr.connect(rfidMgr.getAttachedUsbDevice());
                    }
                });

                createBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: create reader button clicked");
                        rfidMgr.createReader();
                    }
                });

                readBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: clicked");
                        mRfidReader = rfidMgr.getReader(); //burak needs to test this
                        Log.d(TAG, "onClick: is RFID reader really avaialble? " + mRfidReader.available());
                        mRfidReader.setOnTagReadListener(dataListener);
                        mRfidReader.read();



                    }
                });

                stopReadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRfidReader.stopRead();
                    }
                });
            }
        });
    }

    private OnTagReadListener dataListener = new OnTagReadListener() {
        @Override
        public void onTagRead(TagReadData[] tagReadData) {
            Log.d(TAG, "onTagRead: tag is read!!!!!!");
        }
    };


    @Override
    public void onDeviceConnected(Object o) {
        Log.d(TAG, "onDeviceConnected: connected");


    }

    @Override
    public void onDeviceDisconnected(Object o) {
        Log.d(TAG, "onDeviceDisconnected: disconnected");
    }

    @Override
    public void onUsbDeviceAttached(Object o) {
        Log.d(TAG, "onUsbDeviceAttached: attached");
        Log.d(TAG, "onDeviceConnected: connected status is " + rfidMgr.isConnected());
        //rfidMgr.createReader();

    }

    @Override
    public void onUsbDeviceDetached(Object o) {
        Log.d(TAG, "onUsbDeviceDetached: deattached");

        // if you call scanAndConnect() method here, it will establish BT connection after you remove handheld from bracket.
        //scanAndConnect();

    }

    @Override
    public void onReaderCreated(boolean b, RfidReader rfidReader) {
        Log.d(TAG, "onReaderCreated: Reader created");


    }

    @Override
    public void onRfidTriggered(boolean b) {
        Log.d(TAG, "onRfidTriggered: trigger pressed");
    }

    @Override
    public void onTriggerModeSwitched(TriggerMode triggerMode) {

    }

    @Override
    public void onReceivedFindingTag(int i) {

    }

    @Override
    public void onTagRead(TagReadData[] tagReadData) {
        Log.d(TAG, "onTagRead: tag Read");
    }

    public void checkPermissions() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: permission required");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.d(TAG, "checkPermissions: permission granted");
        }

    }

    private BluetoothAdapter.LeScanCallback btScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            //Log.d(TAG, "onLeScan: device found " + device + " with RSSI level " + rssi);
        }
    };
}