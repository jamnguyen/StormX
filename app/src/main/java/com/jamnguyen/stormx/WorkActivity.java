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
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;

public class WorkActivity extends Activity implements CvCameraViewListener2
{
    private XCameraView     m_OpenCvCameraView;
    private boolean         m_isBallOnScreen = false;

    private XBluetooth      m_XBluetooth;
    private boolean         m_isBluetoothConnected = false;
    private XDetector       m_XDetector;
    private Handler         m_Handler;                  //Main handler that will receive callback notifications
    private String          m_MsgFromArduino;

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

        m_MsgFromArduino = "<Nothing received>";
        //Camera
        m_OpenCvCameraView = (XCameraView) findViewById(R.id.activity_java_surface_view);
        m_OpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        m_OpenCvCameraView.setCvCameraViewListener(this);

        //Bluetooth
        Intent newint = getIntent();
        String address = newint.getStringExtra(SetupActivity.EXTRA_ADDRESS);

        //Init handler for message from XBluetooth
        initHandler();

        m_XBluetooth = new XBluetooth(address, getApplicationContext(), m_Handler);
        m_XBluetooth.init();

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

        //If Bluetooth connection fail then there's nothing to do
        if(m_isBluetoothConnected)
        {
            //Put all processing here---------------------------------------------------------------
            m_XDetector.circleDectect(inputFrame);
            if (m_isBluetoothConnected) {
                if (m_XDetector.isBallDetected()) {
                    if (!m_XBluetooth.getPrevSentMsg().equals("TO")) {
                        m_XBluetooth.TurnOnLed();
                    }
                } else {
                    if (!m_XBluetooth.getPrevSentMsg().equals("TF")) {
                        m_XBluetooth.TurnOffLed();
                    }
                }
            }
            //--------------------------------------------------------------------------------------
        }

        //Showing statuses
        Utils.drawString(mRgba, m_MsgFromArduino, 20, 40);

        return mRgba;
    }

    //Bluetooth handling----------------------------------------------------------------------------
    private void initHandler()
    {
        m_Handler = new Handler()
        {
            //Catch message from XBluetooth
            public void handleMessage(android.os.Message msg)
            {
                //Receive message
                if(msg.what == XBluetooth.MESSAGE_READ)
                {
                    String readMessage = null;
                    try
                    {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        m_MsgFromArduino = readMessage;
                    }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                //Get connecting status
                if(msg.what == XBluetooth.CONNECTING_STATUS)
                {
                    if(msg.arg1 == 1)
                    {
                        Utils.toastShort("Connected to Device: " + (msg.obj), getApplicationContext());
                        m_isBluetoothConnected = true;
                    }
                    else
                    {
                        Utils.toastShort("Connection Failed", getApplicationContext());
                    }
                }
            }
        };
    }
}