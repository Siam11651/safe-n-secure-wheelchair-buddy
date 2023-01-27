package com.example.safensecurewheelchairbuddy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScannedDeviceAdapter extends RecyclerView.Adapter<ScannedDeviceViewHolder>
{
    private int count;
    private Context context;
    private ArrayList<Device> scannedDevicesList;

    public ScannedDeviceAdapter(Context context, ArrayList<Device> scannedDevicesList)
    {
        count = 0;
        this.context = context;
        this.scannedDevicesList = scannedDevicesList;
    }

    @NonNull
    @Override
    public ScannedDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ScannedDeviceViewHolder(new ScannedDeviceView(context));
    }

    @Override
    public void onBindViewHolder(@NonNull ScannedDeviceViewHolder holder, int position)
    {
        ((ScannedDeviceView)holder.itemView).SetDevice(scannedDevicesList.get(position));
        ((ScannedDeviceView)holder.itemView).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

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
