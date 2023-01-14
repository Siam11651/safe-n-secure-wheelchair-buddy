package com.a2g5.safensecurewheelchairbuddy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    public static BluetoothAdapter bluetoothAdapter;
    private LinearLayout bluetoothDevicesList;

    private void RefreshDevices()
    {
        bluetoothDevicesList.removeAllViews();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                RequestBluetoothPermission();
            }
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        Set<BluetoothDevice> bluetoothDeviceSet = bluetoothAdapter.getBondedDevices();

        for(BluetoothDevice device : bluetoothDeviceSet)
        {
            View cardView = getLayoutInflater().inflate(R.layout.sample_bluetooth_card, bluetoothDevicesList, false);

            ((TextView) cardView.findViewById(R.id.device_name)).setText(device.getName());
            ((TextView) cardView.findViewById(R.id.device_address)).setText(device.getAddress());
            cardView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder phoneNumberUploader = new AlertDialog.Builder(MainActivity.this);
                    EditText editText = new EditText(phoneNumberUploader.getContext());
                    BluetoothSocket bluetoothSocket;
                    OutputStream outputStream = null;

                    if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                    {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        {
                            RequestBluetoothPermission();
                        }
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }

                    try
                    {
                        bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                        bluetoothSocket.connect();

                        outputStream = bluetoothSocket.getOutputStream();
                    }
                    catch(IOException e)
                    {
                        throw new RuntimeException(e);
                    }

                    OutputStream finalOutputStream = outputStream;

                    editText.setHint("01234567890");
                    editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                    phoneNumberUploader.setTitle("Set Phone Number");
                    phoneNumberUploader.setView(editText);
                    phoneNumberUploader.setPositiveButton("Set", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if(finalOutputStream != null)
                            {
                                byte[] messageByteArray = editText.getText().toString().getBytes();

                                try
                                {
                                    finalOutputStream.write(messageByteArray);
                                }
                                catch(IOException e)
                                {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });
                    phoneNumberUploader.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            try
                            {
                                bluetoothSocket.close();
                            }
                            catch(IOException e)
                            {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    phoneNumberUploader.show();
                }
            });
            bluetoothDevicesList.addView(cardView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void RequestBluetoothPermission()
    {
        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->
        {
            if(!isGranted)
            {
                NoBLuetoothDialog();
            }
        });

        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
    }

    private void NoBLuetoothDialog()
    {
        AlertDialog.Builder noBluetoothHardwareAlertBuilder = new AlertDialog.Builder(this);

        noBluetoothHardwareAlertBuilder.setMessage("Device has no bluetooth hardware");
        noBluetoothHardwareAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
                System.exit(0);
            }
        });

        noBluetoothHardwareAlertBuilder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothDevicesList = findViewById(R.id.bluetooth_devices_list);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {
            RequestBluetoothPermission();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        if(bluetoothAdapter == null)
        {
            NoBLuetoothDialog();

            return;
        }

        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            {
                RequestBluetoothPermission();
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }

           ActivityResultLauncher<Intent> enableBluetoothActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
           {
               @Override
               public void onActivityResult(ActivityResult result)
               {
                   if(result.getResultCode() == Activity.RESULT_CANCELED)
                   {
                        NoBLuetoothDialog();
                   }
               }
           });

            enableBluetoothActivity.launch(enableBluetoothIntent);
        }

        RefreshDevices();

        Button refreshButton = findViewById(R.id.paired_devices_refresh_button);

        refreshButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RefreshDevices();
            }
        });

        BroadcastReceiver bluetoothSwitchOffReciever = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED))
                {
                    if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF)
                    {
                        NoBLuetoothDialog();
                    }
                }
            }
        };

        registerReceiver(bluetoothSwitchOffReciever, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }
}