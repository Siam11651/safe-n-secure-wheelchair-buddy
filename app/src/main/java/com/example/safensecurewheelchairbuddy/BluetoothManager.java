package com.example.safensecurewheelchairbuddy;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class BluetoothManager
{
    private static BluetoothManager singleton;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private UUID uuid;

    private BluetoothManager()
    {
        bluetoothDevice = null;
        bluetoothSocket = null;
        uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    }

    public static BluetoothManager GetBluetoothManager()
    {
        if(singleton == null)
        {
            singleton = new BluetoothManager();
        }

        return singleton;
    }

    public void SetBluetoothDevice(BluetoothDevice bluetoothDevice)
    {
        this.bluetoothDevice = bluetoothDevice;
    }

    public void SetBluetoothSocket(BluetoothSocket bluetoothSocket)
    {
        this.bluetoothSocket = bluetoothSocket;
    }

    public BluetoothDevice GetBluetoothDevice()
    {
        return bluetoothDevice;
    }

    public BluetoothSocket GetBluetoothSocket(BluetoothSocket bluetoothSocket)
    {
        return this.bluetoothSocket;
    }

    public void UnsetDevice()
    {
        bluetoothDevice = null;
    }

    public void UnsetSocket() throws IOException
    {
        if(bluetoothSocket != null)
        {
            bluetoothSocket.close();
        }

        bluetoothSocket = null;
    }

    public UUID GetUUID()
    {
        return uuid;
    }
}
