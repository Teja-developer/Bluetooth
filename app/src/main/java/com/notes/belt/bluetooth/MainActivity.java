package com.notes.belt.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    private Button mButton;
    private Button mFind;
    private static final int REQUEST_ENABLE_BT = 1;
    Set<BluetoothDevice> pairedDevices;
    ListView scanListView;

    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.button);
        mFind = findViewById(R.id.button2);
        scanListView = (ListView) findViewById(R.id.listView);
        //scanListView.setOnItemClickListener(MainActivity.this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedDevices = mBluetoothAdapter.getBondedDevices();



        if (mBluetoothAdapter == null) {
            Log.v("Blue", "No support");
        } else {
            Log.v("Blue", "Supports");
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBlue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBlue, REQUEST_ENABLE_BT);
                    Log.v("Blue", "Enabled");

                    //For getting the info of paired devices

                    if (pairedDevices.size() > 0) {
                        Log.v("Blue", "There are paired devices");
                        for (BluetoothDevice device : pairedDevices) {
                            String name = device.getName();
                            String MACAdd = device.getAddress();
                            Log.v("Blue", name + "   " + MACAdd);
                        }
                    }
                }
            }
        });

        mFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Blue", "Finding devices");
                mBluetoothAdapter.startDiscovery();
                Log.v("Blue", "discovery");


            }
        });
        //Discovery
        IntentFilter discoverDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, discoverDevices);

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
        scanListView.setAdapter(arrayAdapter);

        //Pairing
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

   private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("Blue", "Entered");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArrayList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.v("Blue", deviceName + " " + deviceHardwareAddress);
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
            {
                BluetoothDevice device1 = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Bonded already
                if(device1.getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    Log.v("Blue" , "Bonded");
                }
                //Create a bond
                if(device1.getBondState() == BluetoothDevice.BOND_BONDING)
                {
                    Log.v("Blue" , "Bonding");
                }
                //Break a bond
                if(device1.getBondState() == BluetoothDevice.BOND_NONE)
                {
                    Log.v("Blue" , "No Bonding");
                }
            }
        }
    };


}
