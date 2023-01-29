package com.example.safensecurewheelchairbuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

import javax.sql.PooledConnection;

public class ConnectedActivity extends AppCompatActivity
{
    static class PollRunner implements Runnable {
        private boolean keepRunning;
        private final ImageButton leftButton;
        private final ImageButton rightButton;
        private final ImageButton forwardButton;
        private final ImageButton backwardButton;

        public PollRunner(ImageButton forwardButton, ImageButton backwardButton, ImageButton leftButton, ImageButton rightButton)
        {
            this.forwardButton = forwardButton;
            this.backwardButton = backwardButton;
            this.leftButton = leftButton;
            this.rightButton = rightButton;
            keepRunning = true;
        }

        private synchronized void SyncedRun()
        {
            BluetoothManager bluetoothManager = BluetoothManager.GetBluetoothManager();

            Log.println(Log.DEBUG, "start", "loop");

            while(keepRunning)
            {
                try
                {
                    if(leftButton.isPressed())
                    {
                        Log.println(Log.DEBUG, "button", "left");

                        bluetoothManager.GetOutputStream().write("l\\".getBytes());
                    }
                    else if(rightButton.isPressed())
                    {
                        Log.println(Log.DEBUG, "button", "l");

                        bluetoothManager.GetOutputStream().write("r\\".getBytes());
                    }
                    else if(forwardButton.isPressed())
                    {
                        Log.println(Log.DEBUG, "button", "left");

                        bluetoothManager.GetOutputStream().write("f\\".getBytes());
                    }
                    else if(backwardButton.isPressed())
                    {
                        Log.println(Log.DEBUG, "button", "left");

                        bluetoothManager.GetOutputStream().write("b\\".getBytes());
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException();
                }
            }
        }

        @Override
        public void run()
        {
            SyncedRun();
        }

        public void Stop()
        {
            keepRunning = false;
        }
    }

    private Thread inputPollThread;
    private PollRunner pollRunner;

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

        leftButton.setOnTouchListener(new DirectionClickListener("l"));
        rightButton.setOnTouchListener(new DirectionClickListener("r"));
        forwardButton.setOnTouchListener(new DirectionClickListener("f"));
        backwardButton.setOnTouchListener(new DirectionClickListener("b"));

        // pollRunner = new PollRunner(forwardButton, backwardButton, leftButton, rightButton);
        // inputPollThread = new Thread(pollRunner);

        // inputPollThread.start();

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
                            bluetoothManager.GetOutputStream().write(number.getText().toString().getBytes());
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
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // pollRunner.Stop();

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