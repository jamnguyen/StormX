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
    private double              currentDegree = 0;
    private double              MOMENT_360_OF_PHONE = 32;
    private double              DELTA_DEGREE = 15; ///Because of the accuracy, do not set this value higher than 25degree.

    public Gyroscope (Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {

                    sumOfMoment += event.values[1];

                    double currentMoment = sumOfMoment % MOMENT_360_OF_PHONE;
                    currentMoment = currentMoment / MOMENT_360_OF_PHONE;
                    currentDegree = (int) (currentMoment * 360f);
                    if (currentDegree > 180)
                        currentDegree = currentDegree - 360;
                    if (currentDegree < -180)
                        currentDegree = 360 + currentDegree;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**The return value in three of this (camera down).
     * -1    :  Goal is in the left, need rotate right.
     *  0    :  Goal is opposite, go ahead.
     *  1    :  Goal is in the right, need rotate left.
     **/
    public int getCurrentDirection () {
        if (currentDegree > -DELTA_DEGREE && currentDegree < DELTA_DEGREE) {
            return 0;
        } else if (currentDegree < 0){
            return -1;
        } else {
            return 1;
        }
    }

    public double getCurrentDegree () {
        return currentDegree;
    }

    public void setMOMENT_360_OF_PHONE (int moment_360_of_phone) {
        MOMENT_360_OF_PHONE = moment_360_of_phone;
    }
}
