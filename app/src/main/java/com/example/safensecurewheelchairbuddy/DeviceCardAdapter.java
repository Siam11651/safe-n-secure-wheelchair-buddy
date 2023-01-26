package com.example.safensecurewheelchairbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    @Override
    public int getItemCount()
    {
        return count;
    }
}
