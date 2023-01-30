package com.honts.rfidtester;

/*
RFID Tested app for IH40 and IH45 readers
master branch contains the SDK (version 1.1.0) for IH40 SDK
IH45 branch contains the SDK (version 1.1.5) for IH45 SDK
 */

import androidx.appcompat.app.AppCompatActivity;

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
import com.honeywell.rfidservice.rfid.TagReadData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements EventListener, OnTagReadListener {

    private static final String TAG = "RFTester-MainActivity";

    private RfidManager rfidMgr;
    private ExecutorService executorService;

    private Button connectBtn;
    private Button createBtn;
    private Button readBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        execute();


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
                        rfidMgr.createReader();
                    }
                });
                
                readBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: clicked");
                    }
                });
            }
        });
    }


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

    }

    @Override
    public void onUsbDeviceDetached(Object o) {
        Log.d(TAG, "onUsbDeviceDetached: deattached");

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
}