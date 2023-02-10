package com.example.safensecurewheelchairbuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;

import javax.sql.PooledConnection;

public class ConnectedActivity extends AppCompatActivity
{
    static class DirectionClickListener implements View.OnTouchListener
    {
        private final String direction;
        private String state;

        public DirectionClickListener(String direction)
        {
            this.direction = direction;
            state = "$";
        }

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            BluetoothManager bluetoothManager = BluetoothManager.GetBluetoothManager();

            try
            {
                if(state.equals("$") && event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    bluetoothManager.GetOutputStream().write((direction + '\\').getBytes());

                    state = direction;
                }
                else if(!state.equals("$") && event.getAction() == MotionEvent.ACTION_UP)
                {
                    bluetoothManager.GetOutputStream().write("$\\".getBytes());

                    state = "$";
                }
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }

            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        ImageButton leftButton = findViewById(R.id.left_button);
        ImageButton rightButton = findViewById(R.id.right_button);
        ImageButton forwardButton = findViewById(R.id.forward_button);
        ImageButton backwardButton = findViewById(R.id.backward_button);
        Button setNumberButton = findViewById(R.id.set_number_button);
        Switch phoneControlSwitch = findViewById(R.id.switch_phone_control);

        leftButton.setOnTouchListener(new DirectionClickListener("l"));
        rightButton.setOnTouchListener(new DirectionClickListener("r"));
        forwardButton.setOnTouchListener(new DirectionClickListener("f"));
        backwardButton.setOnTouchListener(new DirectionClickListener("b"));

        setNumberButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConnectedActivity.this);

                alertDialogBuilder.setTitle("Set Number");

                EditText number = new EditText(ConnectedActivity.this);

                number.setInputType(InputType.TYPE_CLASS_PHONE);
                number.setHint("Phone number...");

                alertDialogBuilder.setPositiveButton("Set", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        BluetoothManager bluetoothManager = BluetoothManager.GetBluetoothManager();

                        try
                        {
                            bluetoothManager.GetOutputStream().write(("s\\" + number.getText().toString() + "%\\").getBytes());
                        }
                        catch(IOException e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                });

                alertDialogBuilder.setView(number);
                alertDialogBuilder.show();
            }
        });

        phoneControlSwitch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BluetoothManager bluetoothManager = BluetoothManager.GetBluetoothManager();

                try
                {
                    if(((Switch)v).isChecked())
                    {
                        bluetoothManager.GetOutputStream().write("x\\".getBytes());
                    }
                    else
                    {
                        bluetoothManager.GetOutputStream().write("y\\".getBytes());
                    }
                }
                catch(IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });

        BluetoothManager bluetoothManager = BluetoothManager.GetBluetoothManager();

        try
        {
            bluetoothManager.GetOutputStream().write("q\\".getBytes());
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        if(bluetoothManager.GetInputStream().available() > 0)
                        {
                            byte[] byteArray = new byte[bluetoothManager.GetInputStream().available()];

                            bluetoothManager.GetInputStream().read(byteArray);

                            String command = new String(byteArray);

                            if(command.equals("0"))
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        phoneControlSwitch.setChecked(false);
                                    }
                                });
                            }
                            else if(command.equals("1"))
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        phoneControlSwitch.setChecked(true);
                                    }
                                });
                            }
                        }
                    }
                    catch(IOException e)
                    {
                        break;
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        BluetoothManager bluetoothManager = BluetoothManager.GetBluetoothManager();

        try
        {
            bluetoothManager.UnsetSocket();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }

        bluetoothManager.UnsetDevice();
    }
}