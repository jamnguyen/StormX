package com.jamnguyen.stormx;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static android.content.ContentValues.TAG;

public class XDetector
{
    boolean ballDetected = false;
    int ballX = -1;
    int ballY = -1;

    public Mat circleDectect(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
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
//        Imgproc.HoughCircles(grayInput, circles, Imgproc.CV_HOUGH_GRADIENT, 2, grayInput.rows()/8, 200, 100, 0, 0);
        Imgproc.HoughCircles(grayInput, circles, Imgproc.CV_HOUGH_GRADIENT, 2, grayInput.rows()/8, 99, 39, 10, 400);

        if (circles.cols() > 0)
        {
            ballDetected = true;

            //Draw circles
            double circleVec[] = circles.get(0, 0);

            if (circleVec != null) {
                Point center = new Point((int) circleVec[0], (int) circleVec[1]);
                int radius = (int) circleVec[2];

                ballX = (int) circleVec[0];
                ballY = (int) circleVec[1];

                {
                    Imgproc.circle(rgbaInput, center, 3, new Scalar(0, 0, 255), 5);
                    Imgproc.circle(rgbaInput, center, radius, new Scalar(255, 255, 0), 2);
                }
            }
        }
        else
        {
            ballDetected = false;
            ballX = -1;
            ballY = -1;
        }

        return rgbaInput;
    }

    public boolean isBallDetected()
    {
        return ballDetected;
    }

    public int getBallX()
    {
        return ballX;
    }

    public int getBallY()
    {
        return ballY;
    }
}
