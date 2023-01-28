package com.example.safensecurewheelchairbuddy;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

public class BluetoothConnector
{
    private Device device;
    private Activity currentActivity;
    private Context context;

    public BluetoothConnector(Device device, Activity currentActivity, Context context)
    {
        this.device = device;
        this.currentActivity = currentActivity;
        this.context = context;
    }

    public void Connect() throws IOException
    {
        BluetoothManager bluetoothManager = BluetoothManager.GetBluetoothManager();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothManager.SetBluetoothDevice(bluetoothAdapter.getRemoteDevice(device.GetID()));

        bluetoothManager.SetBluetoothSocket(bluetoothManager.GetBluetoothDevice().createInsecureRfcommSocketToServiceRecord(bluetoothManager.GetUUID()));

        Intent intent = new Intent(currentActivity, ConnectedActivity.class);

        currentActivity.startActivity(intent);
    }
}
