package com.jamnguyen.stormx;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class WorkActivity extends Activity implements CvCameraViewListener2
{
    private XCameraView     m_OpenCvCameraView;
    private boolean         m_ballDetected = false;
    private MenuItem        m_menuDetect;
    private MenuItem        m_menuMultiBall;

    private XBluetooth      m_XBluetooth;
    private XDetector       m_XDetector;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    m_OpenCvCameraView.enableView();
//                    m_OpenCvCameraView.setOnTouchListener(WorkActivity.this);
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

        setContentView(R.layout.activity_work);

        //Camera
        m_OpenCvCameraView = (XCameraView) findViewById(R.id.activity_java_surface_view);
        m_OpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        m_OpenCvCameraView.setCvCameraViewListener(this);

        //Bluetooth
        Intent newint = getIntent();
        String address = newint.getStringExtra(SetupActivity.EXTRA_ADDRESS);
        m_XBluetooth = new XBluetooth(address, getApplicationContext());
        boolean bluetoothConnected = m_XBluetooth.Init();

        m_XDetector = new XDetector();
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
        if (!OpenCVLoader.initDebug())
        {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        }
        else
        {
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
        Mat mRgba = inputFrame.rgba();

        m_XDetector.circleDectect(inputFrame);
        if(m_XBluetooth.isConnected()) {
            if (m_XDetector.isBallDetected()) {
                m_XBluetooth.TurnOnLed();
            } else {
                m_XBluetooth.TurnOffLed();
            }
        }

//        Transpose
//        Mat mRgbaT = mRgba.t();
//        Core.flip(mRgba.t(), mRgbaT, 1);
//        Imgproc.resize(mRgbaT, mRgbaT, mRgba.size());

        return mRgba;
    }

    //Options Menu----------------------------------------------------------------------------------
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //Ball detect menu
//        m_menuDetect = menu.add("BALL DETECT: OFF");
//
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        int itemID = item.getItemId();
//
//        if(itemID == MENU_ID_BALL_DETECT)
//        {
//            m_tryDetectBall = !m_tryDetectBall;
//
//            if(m_tryDetectBall)
//            {
//                m_menuDetect.setTitle("BALL DETECT: ON");
//            }
//            else
//            {
//                m_menuDetect.setTitle("BALL DETECT: OFF");
//            }
//
//        }
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
//
//        return true;
//    }

//    @SuppressLint("SimpleDateFormat")
//    @Override
//    public boolean onTouch(View v, MotionEvent event)
//    {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//        String currentDateandTime = sdf.format(new Date());
//        String fileName = Environment.getExternalStorageDirectory().getPath() +
//                "/sample_picture_" + currentDateandTime + ".jpg";
//        mOpenCvCameraView.takePicture(fileName);
//        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
//        return false;
//    }
}