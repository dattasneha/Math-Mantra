package com.zendalona.mathmantra.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SensorUtility implements SensorEventListener {

    private static final float SHAKE_THRESHOLD = 2000.0f; // Threshold for detecting shake

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] acceleration;
    private float[] geoMagnetic;
    private boolean isDeviceShaken = false;
    private float lastX, lastY, lastZ;
    private long lastUpdate = 0;

    private MutableLiveData<String> _directionLiveData = new MutableLiveData<>("");
    public LiveData<String> directionLiveData = _directionLiveData;
    public SensorUtility(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(context, "Accelerometer Sensor Unavailable", Toast.LENGTH_SHORT).show();
            }
            if (magnetometer != null) {
                sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(context, "Magnetometer Sensor Unavailable", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Sensor Manager Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void unregisterListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceleration = event.values;
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
        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geoMagnetic = event.values;
        }

        if (acceleration != null && geoMagnetic != null) {
            float[] rotationMatrix = new float[9];
            if (SensorManager.getRotationMatrix(rotationMatrix, null, acceleration, geoMagnetic)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);

                float azimuth = (float) Math.toDegrees(orientation[0]);
                if (azimuth < 0) {
                    azimuth += 360;
                }
                Log.d("tag", "hello");
                updateDirection(azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No need to handle accuracy changes for this use case
    }

    public boolean isDeviceShaken() {
        boolean shaken = isDeviceShaken;
        isDeviceShaken = false; // Reset the flag
        return shaken;
    }

    private void updateDirection(float azimuth) {
        Log.d("tag", "hi");
        String direction = "Unknown";
        if (azimuth >= 337.5 || azimuth < 22.5) {
            direction = "North";
        } else if (azimuth >= 22.5 && azimuth < 67.5) {
            direction = "North-East";
        } else if (azimuth >= 67.5 && azimuth < 112.5) {
            direction = "East";
        } else if (azimuth >= 112.5 && azimuth < 157.5) {
            direction = "South-East";
        } else if (azimuth >= 157.5 && azimuth < 202.5) {
            direction = "South";
        } else if (azimuth >= 202.5 && azimuth < 247.5) {
            direction = "South-West";
        } else if (azimuth >= 247.5 && azimuth < 292.5) {
            direction = "West";
        } else if (azimuth >= 292.5 && azimuth < 337.5) {
            direction = "North-West";
        }
        Log.d("tag", direction);
        String currentValue = directionLiveData.getValue();
        if (currentValue == null || !currentValue.equals(direction)) {
            _directionLiveData.setValue(direction);
        }
        Log.d("tag", directionLiveData.getValue());
    }
}
