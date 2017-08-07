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
    private double              sumOfMoment = 0;
	private long m_TimeForGetMoment = 0;

    public Gyroscope (Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sumOfMoment = 0;
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
					if(System.currentTimeMillis() -  m_TimeForGetMoment < XConfig.TIME_DELAY_FOR_GET_MOMENT)
						return;
                    sumOfMoment = event.values[1];
					m_TimeForGetMoment = System.currentTimeMillis();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    
	public double getMoment()
	{
		return sumOfMoment;
	}
}
