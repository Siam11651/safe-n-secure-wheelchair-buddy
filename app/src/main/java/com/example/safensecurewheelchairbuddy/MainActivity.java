package com.example.safensecurewheelchairbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private void SetDeviceList()
    {
        File devicesFile = new File(getFilesDir(), "devices.json");
        JSONObject jsonObject = null;

        try
        {
            devicesFile.createNewFile();
            FileInputStream devicesListFileInputStream = new FileInputStream(devicesFile);
            byte[] fileByteArray = new byte[(int)devicesListFileInputStream.getChannel().size()];

            devicesListFileInputStream.read(fileByteArray, 0, (int)devicesListFileInputStream.getChannel().size());

            jsonObject = new JSONObject(new String(fileByteArray));

            devicesListFileInputStream.close();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        catch(JSONException e)
        {
            // json parse error, expected in first use when json is empty
        }

        ArrayList<Device> devicesList = new ArrayList<Device>();

        if(jsonObject != null)
        {
            try
            {
                JSONArray jsonArray = jsonObject.getJSONArray("devices");

                for(int i = 0; i < jsonArray.length(); ++i)
                {
                    JSONObject jsonObjectArrayElement = (JSONObject)jsonArray.get(i);
                    Device device = new Device(jsonObjectArrayElement.getString("name"), jsonObjectArrayElement.getString("id"));

                    devicesList.add(device);
                }
            }
            catch(JSONException e)
            {
                throw new RuntimeException(e);
            }
        }

        TextView noDeviceTextView = findViewById(R.id.no_device_text_view);
        RecyclerView devicesListRecyclerView = findViewById(R.id.devices_list_recycler_view);

        if(devicesList.size() > 0)
        {
            noDeviceTextView.setVisibility(TextView.GONE);
        }

        devicesListRecyclerView.setAdapter(new DeviceCardAdapter(this, devicesList));

        if(devicesListRecyclerView.getAdapter() != null)
        {
            for(int i = 0; i < devicesList.size(); ++i)
            {
                devicesListRecyclerView.getAdapter().notifyItemInserted(i);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetDeviceList();
    }
}