package com.jamnguyen.stormx;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.List;
import java.util.Vector;

import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.minEnclosingCircle;

public class XDetector
{
    public int                      SCREEN_WIDTH;
    public int                      SCREEN_HEIGHT;
    private Context                 m_appContext;

    private ColorBlobDetector       m_BlobDetectorPink;

    private ColorBlobDetector       m_BlobDetectorOrange;
    private ColorBlobDetector       m_BlobDetectorGreen;
    private Scalar                  m_BlobColorHsv;
    private Scalar                  m_BlobColorRgba;
    private Mat                     m_Spectrum;
    private Scalar                  m_brightness;
    private MatOfPoint              m_ballContour;
    private Scalar                  m_circleColor;
    private Scalar                  m_contourColor;
    private int                     m_middleLine;

    private boolean                 m_isBallOnScreen = false;
    private int                     m_ballX = -1;
    private int                     m_ballY = -1;
    private double                  m_ballArea;
    private Point                   m_ballCenter;
    private int                     m_ballRadius;
    private double                  m_screenArea;
    private Point                   m_midUpPoint;
    private Point                   m_midDownPoint;
    private Point                   m_midUpLeftPoint;
    private Point                   m_midDownLeftPoint;
    private Point                   m_midUpRightPoint;
    private Point                   m_midDownRightPoint;
    private boolean                 m_isDetectBall;



    public void 	setDetectBall(boolean value)
	{
		m_isDetectBall = value;
	}
	public boolean 	getDetectBall()
	{
		return m_isDetectBall;
	}

    public XDetector(Context context, int screenWidth, int screenHeight)
    {
        m_appContext = context;
        SCREEN_WIDTH = screenWidth;
        SCREEN_HEIGHT = screenHeight;
       //USE_TRANSPOSE_MODE = isStormX;
        m_BlobDetectorPink = new ColorBlobDetector();
        m_BlobDetectorOrange = new ColorBlobDetector();
        m_BlobDetectorGreen = new ColorBlobDetector();
        m_Spectrum = new Mat();
        m_BlobColorRgba = new Scalar(255);
        m_BlobColorHsv = new Scalar(255);
        m_brightness = new Scalar(255);
        m_contourColor = new Scalar(255,0,0,255);
        m_circleColor = new Scalar(0, 0, 255, 255);
        m_screenArea = screenWidth*screenHeight;

        if(XConfig.USE_TRANSPOSE_MODE)
        {
            m_middleLine = SCREEN_HEIGHT/2 + XConfig.MIDDLE_OFFSET;

            m_midUpPoint = new Point(SCREEN_WIDTH, m_middleLine);
            m_midUpLeftPoint = new Point(SCREEN_WIDTH, m_middleLine - XConfig.MIDDLE_DELTA);
            m_midUpRightPoint = new Point(SCREEN_WIDTH, m_middleLine + XConfig.MIDDLE_DELTA);

            m_midDownPoint = new Point(0, m_middleLine);
            m_midDownLeftPoint = new Point(0, m_middleLine - XConfig.MIDDLE_DELTA);
            m_midDownRightPoint = new Point(0, m_middleLine + XConfig.MIDDLE_DELTA);
        }
        else
        {
            m_middleLine = SCREEN_WIDTH/2 + XConfig.MIDDLE_OFFSET;

            m_midUpPoint = new Point(m_middleLine, 0);
            m_midUpLeftPoint = new Point(m_middleLine - XConfig.MIDDLE_DELTA, 0);
            m_midUpRightPoint = new Point(m_middleLine + XConfig.MIDDLE_DELTA, 0);

            m_midDownPoint = new Point(m_middleLine, SCREEN_HEIGHT);
            m_midDownLeftPoint = new Point(m_middleLine - XConfig.MIDDLE_DELTA, SCREEN_HEIGHT);
            m_midDownRightPoint = new Point(m_middleLine + XConfig.MIDDLE_DELTA, SCREEN_HEIGHT);
        }

        m_BlobColorHsv = new Scalar(XConfig.COLOR_PINK[0], XConfig.COLOR_PINK[1], XConfig.COLOR_PINK[2], 0.0);
        m_BlobDetectorPink.setHsvColor(m_BlobColorHsv);
        m_BlobColorHsv = new Scalar(XConfig.COLOR_ORANGE[0], XConfig.COLOR_ORANGE[1], XConfig.COLOR_ORANGE[2], 0.0);
        m_BlobDetectorOrange.setHsvColor(m_BlobColorHsv);
        m_BlobColorHsv = new Scalar(XConfig.COLOR_GREEN[0], XConfig.COLOR_GREEN[1], XConfig.COLOR_GREEN[2], 0.0);
        m_BlobDetectorGreen.setHsvColor(m_BlobColorHsv);
    }

    public void init()
    {
        m_isDetectBall = true;
        m_ballArea = 0;
    }

    //Circle detecting------------------------------------------------------------------------------
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
            m_isBallOnScreen = true;

            //Draw circles
            double circleVec[] = circles.get(0, 0);

            if (circleVec != null) {
                Point center = new Point((int) circleVec[0], (int) circleVec[1]);
                int radius = (int) circleVec[2];

                m_ballX = (int) circleVec[0];
                m_ballY = (int) circleVec[1];

                {
                    Imgproc.circle(rgbaInput, center, 3, new Scalar(0, 0, 255), 5);
                    Imgproc.circle(rgbaInput, center, radius, new Scalar(255, 255, 0), 2);
                }
            }
        }
        else
        {
            m_isBallOnScreen = false;
            m_ballX = -1;
            m_ballY = -1;
        }

        return rgbaInput;
    }

    public int getBallX()
    {
        return m_ballX;
    }

    public int getBallY()
    {
        return m_ballY;
    }

    //----------------------------------------------------------------------------------------------

    //Color detecting-------------------------------------------------------------------------------
    public Mat colorDetect(Mat rgbaInput)
    {
		List<MatOfPoint> contours;
		if(m_isDetectBall)
		{
            m_BlobDetectorPink.process(rgbaInput);
            m_BlobDetectorOrange.process(rgbaInput);
			contours = m_BlobDetectorPink.getContours();
			contours.addAll(m_BlobDetectorOrange.getContours());
		}
		else
		{
            m_BlobDetectorGreen.process(rgbaInput);
			contours = m_BlobDetectorGreen.getContours();
			// contours.addAll(m_BlobDetectorOrange.getContours());
		}

		Imgproc.drawContours(rgbaInput, contours, -1, m_contourColor);

        //Get biggest area contour
        int indexOfBiggestContour = GetIndexOfBiggestContour(contours);
        if(indexOfBiggestContour > - 1)
        {
            //Camera saw the ball
            //Handle catching
            m_isBallOnScreen = true;
            m_ballContour = contours.get(indexOfBiggestContour);

            if(XConfig.DETECT_COLOR_WITH_CIRCLE)
            {
                /// Hough circle
                MatOfPoint2f current_contour = new MatOfPoint2f(m_ballContour.toArray());
                m_ballCenter = new Point();
                float []current_contour_radius = new float[1];
                minEnclosingCircle(current_contour, m_ballCenter, current_contour_radius);

                if (current_contour_radius[0] > 0) {

                    m_ballRadius = (int) current_contour_radius[0];
//                    m_ballArea = 3.14 * m_ballRadius * m_ballRadius;
                }
                else
                {
                    m_ballRadius = 0;
                }
            }
            else
            {
                m_ballCenter = GetCenterPointOfContour(m_ballContour);
            }
        }
        else
        {
            //No ball was seen
            //Arduino resume running
            m_isBallOnScreen = false;
            m_ballContour = null;
//                IM_Update(false);
        }

        //Draw center point
        if(m_ballContour != null) {
            Imgproc.circle(rgbaInput, GetCenterPointOfContour(m_ballContour), 20, m_circleColor, -1);
            if(XConfig.DETECT_COLOR_WITH_CIRCLE)
            {
                Imgproc.circle(rgbaInput, m_ballCenter, m_ballRadius, new Scalar(100, 255, 50));
            }

            Mat colorLabel = rgbaInput.submat(4, 68, 4, 68);
            colorLabel.setTo(m_BlobColorRgba);

            Mat spectrumLabel = rgbaInput.submat(4, 4 + m_Spectrum.rows(), 70, 70 + m_Spectrum.cols());
            m_Spectrum.copyTo(spectrumLabel);
        }

        return rgbaInput;
    }

    public void getColorOnTouch(Mat Rgba, int frameWidth, int frameHeight, MotionEvent event)
    {
        int cols = Rgba.cols();
        int rows = Rgba.rows();

        int xOffset = (frameWidth - cols) / 2;
        int yOffset = (frameHeight - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

//        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;

        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = Rgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        //Get Color---------------------------------------------------------------------------------
//        m_BlobColorHsv = Core.sumElems(touchedRegionHsv);
//        int pointCount = touchedRect.width*touchedRect.height;
//        for (int i = 0; i < m_BlobColorHsv.val.length; i++)
//            m_BlobColorHsv.val[i] /= pointCount;
////
//////        m_BlobColorRgba = converScalarHsv2Rgba(m_BlobColorHsv);
////
//        Utils.toastLong("Touched HSV color: (" + m_BlobColorHsv.val[0] + ", " + m_BlobColorHsv.val[1] +
//                ", " + m_BlobColorHsv.val[2] + ", " + m_BlobColorHsv.val[3] + ")", m_appContext);
//        m_BlobDetectorPink.setHsvColor(m_BlobColorHsv);
//
//        if(!Gameplay.ANDROID_STARTED)
//        {
//            Gameplay.setAndroidStarted(true);
//            Gameplay.setAndroidInitialized(false);
//        }
//        else
//        {
//            Gameplay.setAndroidStarted(false);
//        }

        //------------------------------------------------------------------------------------------


        if(!Gameplay.ANDROID_STARTED)
        {
            Utils.toastShort("ANDROID STARTED", m_appContext);
            Gameplay.setAndroidStarted(true);
            Gameplay.setAndroidInitialized(false);
        }
        else
        {
            Utils.toastShort("ANDROID STOP", m_appContext);
            Gameplay.setAndroidStarted(false);
        }

        touchedRegionRgba.release();
        touchedRegionHsv.release();
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor)
    {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    private int GetIndexOfBiggestContour(List<MatOfPoint> contours)
    {

        int index = -1;
        if(contours.size() == 0)
            return index;

        double area = 0;
        for (int i = 0; i < contours.size(); i++) {
            if (contourArea(contours.get(i)) > area) {
                area = contourArea(contours.get(i));
                index = i;
            }
        }

        if(index > -1)
        {
            m_ballArea = area;
        }
        else
        {
            m_ballArea = 0;
        }
        return index;
    }

    private Point GetCenterPointOfContour(MatOfPoint contour)
    {
        Moments m = Imgproc.moments(contour);
        int x = (int)(m.m10 / m.m00);
        int y =(int)(m.m01 / m.m00);
        return new Point(x, y);
    }

    public Point getBallCenter()
    {
        if(m_ballContour != null)
        {
            return GetCenterPointOfContour(m_ballContour);
        }
        else
        {
            return null;
        }
    }

    public int getMiddleLine()
    {
        return m_middleLine;
    }

    public double getBallArea()
    {
        return m_ballArea;
    }

    public int getBallRadius()
    {
        return m_ballRadius;
    }

    public double getBallDistance()
    {
        if(m_ballRadius > 0)
            return XConfig.XA_DISTANCE_FACTOR / m_ballRadius;
        else
            return -1;
    }

    public double getScreenArea()
    {
        return m_screenArea;
    }


    public boolean isBallOnScreen()
    {
        return m_isBallOnScreen;
    }
    //----------------------------------------------------------------------------------------------
    public int getTransposedX(int yO)
    {
        return yO;
    }

    public int getTransposedY(int xO)
    {
        return (SCREEN_WIDTH - xO);
    }

    public void drawForwardRange(Mat rgba)
    {
        Imgproc.line(rgba, m_midUpPoint, m_midDownPoint, new Scalar(255, 0, 0, 255));
        Imgproc.line(rgba, m_midUpLeftPoint, m_midDownLeftPoint, new Scalar(255, 255, 0, 255));
        Imgproc.line(rgba, m_midUpRightPoint, m_midDownRightPoint, new Scalar(255, 255, 0, 255));
    }
}
