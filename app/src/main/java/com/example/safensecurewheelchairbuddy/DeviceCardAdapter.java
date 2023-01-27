package com.example.safensecurewheelchairbuddy;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class DeviceCardAdapter extends RecyclerView.Adapter<DeviceCardViewHolder>
{
    private int count;
    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<Device> devicesList;

    public DeviceCardAdapter(Context context, ArrayList<Device> devicesList)
    {
        count = 0;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.devicesList = devicesList;
    }

    @NonNull
    @Override
    public DeviceCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new DeviceCardViewHolder(new DeviceCardView(context));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceCardViewHolder holder, int position)
    {
        ((DeviceCardView)holder.itemView).SetDevice(devicesList.get(position));
        ((DeviceCardView)holder.itemView).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(devicesList.get(holder.getAdapterPosition()).GetID());
                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                try
                {
                    BluetoothSocket bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(BluetoothManager.GetBluetoothManager().GetUUID());

                    BluetoothManager.GetBluetoothManager().SetBluetoothDevice(bluetoothDevice);
                    BluetoothManager.GetBluetoothManager().GetBluetoothSocket(bluetoothSocket);

                    // change activity
                }
                catch(IOException e)
                {
                    // implement handler later ig
                    throw new RuntimeException(e);
                }
            }
        });

        ++count;
    }

    @Override
    public int getItemCount()
    {
        return count;
    }
}
