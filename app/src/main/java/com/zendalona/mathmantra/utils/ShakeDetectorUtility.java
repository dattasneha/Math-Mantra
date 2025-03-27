package com.zendalona.mathmantra.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.Toast;

public class ShakeDetectorUtility extends SensorUtility{

    // Threshold for detecting shake
    private static final float SHAKE_THRESHOLD = 2000.0f;

    private Sensor accelerometer;
    private boolean isDeviceShaken = false;
    private float lastX, lastY, lastZ;
    private long lastUpdate = 0;

    public ShakeDetectorUtility(Context context) {
        super(context);
        if (getSensorManager() != null) {
            accelerometer = getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                getSensorManager().registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            }else {
                Toast.makeText(context, "Accelerometer Sensor Unavailable", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Sensor Manager Unavailable", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void unregisterListener() {
        super.unregisterListener();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        super.onSensorChanged(event);
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastUpdate) > 100) {
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    isDeviceShaken = true;
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    public boolean isDeviceShaken() {
        boolean shaken = isDeviceShaken;
        isDeviceShaken = false; // Reset the flag
        return shaken;
    }

}
