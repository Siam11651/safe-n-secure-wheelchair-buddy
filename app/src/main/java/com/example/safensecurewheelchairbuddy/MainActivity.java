package com.example.safensecurewheelchairbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.renderscript.RenderScript;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.contentcapture.ContentCaptureSession;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity
{
    private void RefreshPairedDevices(View view)
    {
        LinearLayout scannedDevicesList = view.findViewById(R.id.scanned_devices_list);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ArrayList<Device> scannedDevices = new ArrayList<>();
        Set<BluetoothDevice> bluetoothDeviceSet = bluetoothAdapter.getBondedDevices();

        for(BluetoothDevice bluetoothDevice : bluetoothDeviceSet)
        {
            Device device = new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress());

            scannedDevices.add(device);
        }

        TextView emptyPairedDevicesTextView = view.findViewById(R.id.empty_paired_device_list_text_view);

        if(scannedDevices.size() > 0)
        {
            emptyPairedDevicesTextView.setVisibility(TextView.GONE);
            scannedDevicesList.removeAllViews();
        }
        else
        {
            emptyPairedDevicesTextView.setVisibility(TextView.VISIBLE);
        }

        for(int i = 0; i < scannedDevices.size(); ++i)
        {
            View deviceCard = getLayoutInflater().inflate(R.layout.sample_scanned_device_view, scannedDevicesList, false);

            ((TextView)deviceCard.findViewById(R.id.device_name_text_view)).setText(scannedDevices.get(i).GetName());
            ((TextView)deviceCard.findViewById(R.id.device_id_text_view)).setText(scannedDevices.get(i).GetID());
            ((CardView)deviceCard).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    File devicesFile = new File(getFilesDir(), "devices.json");
                    JSONObject jsonObject = null;

                    try
                    {
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
                            // handle
                        }
                    }

                    String nameText = ((TextView)v.findViewById(R.id.device_name_text_view)).getText().toString();
                    String idText = ((TextView)v.findViewById(R.id.device_id_text_view)).getText().toString();

                    boolean found = false;

                    for(int i = 0; i < devicesList.size(); ++i)
                    {
                        if(devicesList.get(i).GetID().equals(idText));

                        found = true;

                        break;
                    }

                    if(!found)
                    {
                        devicesList.add(new Device(nameText, idText));

                        if(jsonObject == null)
                        {
                            jsonObject = new JSONObject();
                            JSONArray jsonArray = new JSONArray();

                            try
                            {
                                for(int i = 0; i < devicesList.size(); ++i)
                                {
                                    JSONObject deviceJsonObject = new JSONObject();

                                    deviceJsonObject.put("name", devicesList.get(i).GetName());
                                    deviceJsonObject.put("id", devicesList.get(i).GetID());
                                    jsonArray.put(deviceJsonObject);
                                }

                                jsonObject.put("devices", jsonArray);
                            }
                            catch(JSONException e)
                            {
                                // handle
                            }
                        }
                        else
                        {
                            try
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("devices");
                                JSONObject deviceJsonObject = new JSONObject();

                                deviceJsonObject.put("name", devicesList.get(devicesList.size() - 1).GetName());
                                deviceJsonObject.put("id", devicesList.get(devicesList.size() - 1).GetID());
                                jsonArray.put(deviceJsonObject);
                            }
                            catch(JSONException e)
                            {
                                // handle
                            }
                        }

                        try
                        {
                            FileOutputStream devicesFileOutputStream = new FileOutputStream(devicesFile);
                            String jsonString = jsonObject.toString();
                            byte[] byteArray = jsonString.getBytes();

                            devicesFileOutputStream.write(byteArray);
                            devicesFileOutputStream.close();
                        }
                        catch(IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            scannedDevicesList.addView(deviceCard);
        }
    }

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
        LinearLayout devicesListLinearLayout = findViewById(R.id.devices_list_recycler_view);

        if(devicesList.size() > 0)
        {
            noDeviceTextView.setVisibility(TextView.GONE);
        }

        for(int i = 0; i < devicesList.size(); ++i)
        {
            View view = getLayoutInflater().inflate(R.layout.sample_device_card_view, devicesListLinearLayout, false);

            ((TextView)view.findViewById(R.id.device_name_text_view)).setText(devicesList.get(i).GetName());
            ((TextView)view.findViewById(R.id.device_id_text_view)).setText(devicesList.get(i).GetID());
            devicesListLinearLayout.addView(view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addDeviceButton = findViewById(R.id.add_device_button);

        addDeviceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                View view = getLayoutInflater().inflate(R.layout.sample_connect_bluetooth_list, null, false);

                ((Button)view.findViewById(R.id.refresh_button)).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        RefreshPairedDevices(view);
                    }
                });

                alertDialogBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setTitle("Scanned Devices");
                alertDialogBuilder.setCancelable(false);

                AlertDialog alertDialog = alertDialogBuilder.create();

                RefreshPairedDevices(view);
                alertDialog.setView(view);
                alertDialog.show();
            }
        });

        SetDeviceList();
    }
}