package com.jamnguyen.stormx;

import android.content.Context;
import android.widget.Toast;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static com.jamnguyen.stormx.Gameplay.*;

public class Utils
{
    //Fast way to call Toast
    public static void toastLong(String s, Context context)
    {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public static void toastShort(String s, Context context)
    {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    //Fast way to put text on Mat
    public static void drawString(Mat frame, String content, int x, int y)
    {
        Imgproc.putText(frame, content, new Point(x, y), Core.FONT_HERSHEY_PLAIN, 2.0, new Scalar(255, 255, 0), 2);
    }

    public static String getColorString(int colorCode)
    {
        switch(colorCode)
        {
            case COLOR_NEAR:
                return "NEAR";
            case COLOR_LEFT:
                return "LEFT";
            case COLOR_RIGHT:
                return "RIGHT";
            case COLOR_MIDDLE:
                return "MIDDLE";
            case COLOR_TOO_NEAR:
                return "NEAR";
            case COLOR_TOO_FAR:
                return "FAR";
            default:
                return "ZERO";
        }
    }

    public static String getStateString(int stateCode)
    {
        switch(stateCode)
        {
            case STATE_INIT:
                return "INIT";
            case STATE_FIND_BALL:
                return "FIND_BALL";
            case STATE_FOLLOW_BALL:
                return "FOLLOW_BALL";
            case STATE_CATCH_BALL:
                return "CATCH_BALL";
            case STATE_FIND_GOAL:
                return "FIND_GOAL";
            case STATE_GO_GOAL:
                return "STATE_GO_GOAL";
            case STATE_RELEASE_BALL:
                return "RELEASE_BALL";
            case STATE_CLEAN_PATH:
                return "CLEAN_PATH";
            case STATE_CENTER_BALL:
                return "CENTER_BALL";
            case STATE_WAITING:
                return "WAITING";
            default:
                return "NONE";
        }
    }
}
