package com.zendalona.mathmantra.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.zendalona.mathmantra.R;
import com.zendalona.mathmantra.listener.DirectionChangeListener;

public class DirectionDetectorUtility extends SensorUtility{
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] acceleration;
    private float[] geoMagnetic;
    private float currentAzimuth = 0f;
    private SoundEffectUtility soundEffectUtility;
    private DirectionChangeListener directionChangeListener;
    public DirectionDetectorUtility(Context context, DirectionChangeListener directionChangeListener) {
        super(context);
        this.directionChangeListener = directionChangeListener;
        soundEffectUtility = SoundEffectUtility.getInstance(context,2);
        if (getSensorManager() != null) {
            accelerometer = getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = getSensorManager().getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            if (accelerometer != null) {
                getSensorManager().registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(context, "Accelerometer Sensor Unavailable", Toast.LENGTH_SHORT).show();
            }
            if (magnetometer != null) {
                getSensorManager().registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(context, "Magnetometer Sensor Unavailable", Toast.LENGTH_SHORT).show();
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceleration = event.values;
            if(acceleration[0] > 1.5) {
                soundEffectUtility.setVolume(R.raw.stereo,0.0f,1.0f);
            } else if(acceleration[0] < -1.5) {
                soundEffectUtility.setVolume(R.raw.stereo,0.0f,1.0f);
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
                directionChangeListener.onAzimuthChanged(azimuth);
            }
        }
    }

}
