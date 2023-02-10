package com.example.safensecurewheelchairbuddy;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothManager
{
    private static BluetoothManager singleton;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private UUID uuid;
    private OutputStream outputStream;
    private InputStream inputStream;

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

    public void SetBluetoothSocket(BluetoothSocket bluetoothSocket) throws IOException
    {
        this.bluetoothSocket = bluetoothSocket;

        bluetoothSocket.connect();

        outputStream = bluetoothSocket.getOutputStream();
        inputStream = bluetoothSocket.getInputStream();
    }

    public BluetoothDevice GetBluetoothDevice()
    {
        return bluetoothDevice;
    }

    public BluetoothSocket GetBluetoothSocket()
    {
        return bluetoothSocket;
    }

    public OutputStream GetOutputStream()
    {
        return outputStream;
    }

    public InputStream GetInputStream()
    {
        return inputStream;
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

        if(outputStream != null)
        {
            outputStream.close();
        }

        if(inputStream != null)
        {
            inputStream.close();
        }

        bluetoothSocket = null;
        outputStream = null;
        inputStream = null;
    }

    public UUID GetUUID()
    {
        return uuid;
    }
}
