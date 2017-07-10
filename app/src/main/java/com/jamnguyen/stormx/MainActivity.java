package com.jamnguyen.stormx;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class MainActivity extends Activity implements CvCameraViewListener2, OnTouchListener
{
    private static final String TAG = "StormX: Main";
    private static final int MENU_ID_BALL_DETECT = 0;
    private static final int MENU_ID_MULTI_BALL_DETECT = 1;

    private XCameraView m_OpenCvCameraView;
    private boolean m_tryDetectBall = false;
    private boolean m_detectMultiBall = false;
    private MenuItem m_menuDetect;
    private MenuItem m_menuMultiBall;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    m_OpenCvCameraView.enableView();
                    m_OpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    //Android States--------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        m_OpenCvCameraView = (XCameraView) findViewById(R.id.activity_java_surface_view);

        m_OpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        m_OpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (m_OpenCvCameraView != null)
            m_OpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (m_OpenCvCameraView != null)
            m_OpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height)
    {
    }

    //Camera handling-------------------------------------------------------------------------------
    public void onCameraViewStopped()
    {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame)
    {
        if(m_tryDetectBall)
        {
            return Detector.circleDectect(inputFrame, m_detectMultiBall);
        }
        else
        {
            return inputFrame.rgba();
        }
    }

    //Options Menu----------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Ball detect menu
        m_menuDetect = menu.add("BALL DETECT: OFF");
//        m_menuMultiBall = menu.add("MULTI-BALL TRACKING: OFF");

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();

        if(itemID == MENU_ID_BALL_DETECT)
        {
            m_tryDetectBall = !m_tryDetectBall;

            if(m_tryDetectBall)
            {
                m_menuDetect.setTitle("BALL DETECT: ON");
            }
            else
            {
                m_menuDetect.setTitle("BALL DETECT: OFF");
            }

        }
//        else if(itemID == MENU_ID_MULTI_BALL_DETECT)
//        {
//            m_detectMultiBall = !m_detectMultiBall;
//
//            if(m_detectMultiBall)
//            {
//                m_menuMultiBall.setTitle("MULTI-BALL TRACKING: ON");
//            }
//            else
//            {
//                m_menuMultiBall.setTitle("MULTI-BALL TRACKING: OFF");
//            }
//        }
//
//        m_menuMultiBall.setTitle("MULTI-BALL TRACKING: ON");

        return true;
    }

//    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//        String currentDateandTime = sdf.format(new Date());
//        String fileName = Environment.getExternalStorageDirectory().getPath() +
//                "/sample_picture_" + currentDateandTime + ".jpg";
//        mOpenCvCameraView.takePicture(fileName);
//        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }
}