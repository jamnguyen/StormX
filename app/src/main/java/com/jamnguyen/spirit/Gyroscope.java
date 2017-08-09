package com.jamnguyen.spirit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * Created by khanh.nguyentan on 8/5/2017.
 */

public class Gyroscope {
    private SensorManager       sensorManager;
    private SensorEventListener sensorEventListener;
    private int              sumOfMoment = 0;

    public Gyroscope (Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
                    sumOfMoment += (int)event.values[1];
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    
	public int getMoment()
	{
		return (int)sumOfMoment;
	}
}
