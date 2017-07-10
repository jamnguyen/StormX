package com.jamnguyen.stormx;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static android.content.ContentValues.TAG;

public class Detector
{
    public static Mat circleDectect(CameraBridgeViewBase.CvCameraViewFrame inputFrame, boolean multiCircle)
    {
        //To grayscale
        Mat grayInput = inputFrame.gray();
        Mat rgbaInput = inputFrame.rgba();

        //Mat contains detected circles
        //Number of column is the amount
        Mat circles = new Mat();

        //Reducing noise
        Imgproc.blur(grayInput, grayInput, new Size(7, 7), new Point(2, 2));

        //Hough Circles Transform
        //grayInput.rows()/8: Minimum distance between detected centers
        //200: Upper threshold for the internal Canny edge detector
        //100: Threshold for center detection.
        //0: Minimum radio to be detected. If unknown, put zero as default.
        //0: Maximum radius to be detected. If unknown, put zero as default
        Imgproc.HoughCircles(grayInput, circles, Imgproc.CV_HOUGH_GRADIENT, 2, grayInput.rows()/8, 200, 100, 0, 0);

        int circleAmount = 1;
        if(multiCircle)
        {
            circleAmount = circles.cols();
        }
        if (circles.cols() > 0) {
            for (int x = 0; x < circleAmount; x++) {
                //Draw circles
                double circleVec[] = circles.get(0, x);

                if (circleVec != null) {
                    Point center = new Point((int) circleVec[0], (int) circleVec[1]);
                    int radius = (int) circleVec[2];

                    {
                        Imgproc.circle(rgbaInput, center, 3, new Scalar(0, 0, 255), 5);
                        Imgproc.circle(rgbaInput, center, radius, new Scalar(255, 255, 0), 2);
                    }
                }
            }
        }

        return rgbaInput;
    }
}
