package com.example.safensecurewheelchairbuddy;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.io.IOException;

/**
 * TODO: document your custom view class.
 */
public class DeviceCardView extends View
{
    private Device device;
    private Context context;

    public DeviceCardView(Context context)
    {
        super(context);

        this.context = context;

        init(null, 0);
    }

    public DeviceCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public DeviceCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.DeviceCardView, defStyle, 0);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;
    }

    public void SetDevice(Device device)
    {
        this.device = device;

        ((TextView)findViewById(R.id.device_name_text_view)).setText(device.GetName());
        ((TextView)findViewById(R.id.device_id_text_view)).setText(device.GetID());
    }
}