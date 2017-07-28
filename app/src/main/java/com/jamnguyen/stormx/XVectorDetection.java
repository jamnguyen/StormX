package com.jamnguyen.stormx;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by dung.levan on 27/07/2017.
 */

public class XVectorDetection{
    SensorManager sensorManager = null;
    Sensor rotationVectorSensor = null;
    SensorEventListener rvListener = null;
    int x,y,z;
   // Context context;

    public XVectorDetection(Context mContext){
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Create a listener
        rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                // More code goes here
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix, sensorEvent.values);
                // Remap coordinate system
                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,SensorManager.AXIS_X,SensorManager.AXIS_Z, remappedRotationMatrix);

                // Convert to orientations
                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);
                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                }
                x = (int)orientations[0] + 180;
                y = (int)orientations[1] + 180;
                z = (int)orientations[2] + 180;

              //  tvVector.setText("Vector: " + orientations[0] + " - " + orientations[1] + " -- " + orientations[2]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
// Register it
        sensorManager.registerListener(rvListener,
                rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public int getX()
    {
        return x;
    }

    public int getY ()
    {
        return y;
    }
    public int getZ ()
    {
        return z;
    }
}
