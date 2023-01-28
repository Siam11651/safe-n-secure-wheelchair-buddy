package com.example.safensecurewheelchairbuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class ConnectedActivity extends AppCompatActivity
{
    static class DirectionClickListener implements View.OnClickListener
    {
        private String direction;

        public DirectionClickListener(String direction)
        {
            this.direction = direction;
        }

        @Override
        public void onClick(View v)
        {
            BluetoothManager bluetoothManager = BluetoothManager.GetBluetoothManager();

            try
            {
                bluetoothManager.GetOutputStream().write(direction.getBytes());
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
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

        leftButton.setOnClickListener(new DirectionClickListener("left"));
        rightButton.setOnClickListener(new DirectionClickListener("right"));
        forwardButton.setOnClickListener(new DirectionClickListener("forward"));
        backwardButton.setOnClickListener(new DirectionClickListener("backward"));
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